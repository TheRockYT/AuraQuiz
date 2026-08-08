package one.felsen.auraquiz.ui.screen.deck

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import one.felsen.auraquiz.data.card.CardRepository
import one.felsen.auraquiz.data.deck.DeckEntity
import one.felsen.auraquiz.data.deck.DeckRepository
import one.felsen.auraquiz.ui.UiState
import one.felsen.auraquiz.ui.screen.diolog.DialogComponent
import one.felsen.auraquiz.ui.screen.diolog.ErrorDialog
import one.felsen.auraquiz.ui.screen.diolog.InfoDialog
import one.felsen.auraquiz.ui.screen.diolog.LoadingDialog
import one.felsen.auraquiz.ui.screen.settings.SettingsPlane
import one.felsen.auraquiz.ui.screen.settings.components.MultiSelectableList
import one.felsen.auraquiz.viewmodel.DecksViewModel
import one.felsen.auraquiz.viewmodel.ExportDecksViewModel

@Composable
fun DeckExportScreen(onBack: () -> Unit, deckRepository: DeckRepository, cardRepository: CardRepository) {
    val context = LocalContext.current.applicationContext

    val decksViewModel = viewModel { DecksViewModel(deckRepository) }
    val exportDecksViewModel = viewModel { ExportDecksViewModel(deckRepository, cardRepository) }
    val uiState by decksViewModel.uiState.collectAsStateWithLifecycle()
    var showExportDialog by remember { mutableStateOf(false) }
    var selectedItems by remember { mutableStateOf(setOf<DeckEntity>()) }
    var isUserDataExportSelected by remember { mutableStateOf(false) }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            exportDecksViewModel.exportDecks(context = context, uri = it, exportUserData = isUserDataExportSelected, decks = selectedItems.toList())
        }
    }

    SettingsPlane(title = "Export", onBack = onBack, actions = {
        IconButton(onClick = { showExportDialog = true }, enabled = selectedItems.isNotEmpty()) {
            Icon(
                painter = rememberVectorPainter(Icons.Default.Check), contentDescription = "Add"
            )
        }
    }) {

        when (val state = uiState) {
            is UiState.Error -> {
                ErrorDialog(
                    message = state.message, onDismissRequest = { onBack() })
            }

            is UiState.Loading -> {
                LoadingDialog()
            }

            is UiState.Success -> {
                val decks = state.data

                MultiSelectableList(
                    items = decks,
                    selectedItems = selectedItems,
                    indexId = { deck -> deck.id },
                    onItemClicked = { deck ->
                        selectedItems = if (deck in selectedItems) {
                            selectedItems - deck
                        } else {
                            selectedItems + deck
                        }
                    },
                    content = { index, deck ->
                        Text("${index + 1}. ${deck.name}")
                    })

                if (showExportDialog) {
                    val exportState by exportDecksViewModel.uiState.collectAsStateWithLifecycle()
                    when (val eState = exportState) {
                        is UiState.Error -> {
                            ErrorDialog(
                                message = eState.message, onDismissRequest = { onBack() })
                        }

                        is UiState.Loading -> {
                            LoadingDialog()
                        }

                        is UiState.Success -> {
                            if (eState.data) {
                                InfoDialog(
                                    message = "Export successful",
                                    onDismissRequest = {
                                        onBack()
                                    }
                                )
                            } else {

                                DialogComponent(onDismissRequest = { showExportDialog = false }) {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = "Export",
                                        modifier = Modifier.size(48.dp)
                                    )

                                    Text(
                                        text = "Export",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                                            .clickable {
                                                isUserDataExportSelected = !isUserDataExportSelected
                                            }
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(
                                            text = "Export User Data",
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Checkbox(
                                            checked = isUserDataExportSelected, onCheckedChange = null
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { createDocumentLauncher.launch("aq_export.json") },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            Text("Export")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
