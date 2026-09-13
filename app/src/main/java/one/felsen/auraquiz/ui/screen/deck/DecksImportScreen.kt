package one.felsen.auraquiz.ui.screen.deck

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import one.felsen.auraquiz.data.card.CardRepository
import one.felsen.auraquiz.data.deck.DeckRepository
import one.felsen.auraquiz.ui.UiState
import one.felsen.auraquiz.ui.screen.dialog.ErrorDialog
import one.felsen.auraquiz.ui.screen.dialog.InfoDialog
import one.felsen.auraquiz.ui.screen.dialog.LoadingDialog
import one.felsen.auraquiz.ui.screen.settings.SettingsPlane
import one.felsen.auraquiz.ui.screen.settings.components.ListComponent
import one.felsen.auraquiz.viewmodel.ConflictStrategy
import one.felsen.auraquiz.viewmodel.ImportDecksState
import one.felsen.auraquiz.viewmodel.ImportDecksViewModel
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecksImportScreen(onBack: () -> Unit, uri: Uri, deckRepository: DeckRepository, cardRepository: CardRepository) {
    val context = LocalContext.current.applicationContext
    val importDecksViewModel = viewModel { ImportDecksViewModel(uri, deckRepository, cardRepository) }
    val uiState by importDecksViewModel.uiState.collectAsStateWithLifecycle()
    var selectedStrategy by remember { mutableStateOf(ConflictStrategy.MERGE) }
    var importUserData by remember { mutableStateOf(true) }

    LaunchedEffect(uri) {
        importDecksViewModel.loadFile(context = context)
    }

    SettingsPlane(title = "Import", onBack = onBack, actions = {
        IconButton(onClick = {
            importDecksViewModel.import(selectedStrategy, importUserData)
        }, enabled = true) {
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
                when (val data = state.data) {
                    is ImportDecksState.COMPLETED -> {
                        InfoDialog(
                            message = "Import completed successfully.",
                            onDismissRequest = { onBack() }
                        )
                    }

                    is ImportDecksState.IMPORTING -> {
                        val decks = data.file.decks
                        var expanded by remember { mutableStateOf(false) }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedStrategy.title,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Conflict Strategy") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    ConflictStrategy.entries.forEach { strategy ->
                                        DropdownMenuItem(
                                            text = { Text(strategy.title) },
                                            onClick = {
                                                selectedStrategy = strategy
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = selectedStrategy.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { importUserData = !importUserData }
                                    .padding(vertical = 8.dp)
                            ) {
                                Checkbox(
                                    checked = importUserData,
                                    onCheckedChange = { importUserData = it }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Import user data",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            if (decks != null) {

                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                                Text(
                                    text = "Decks to Import",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                ListComponent(
                                    items = decks,
                                    indexId = { item -> item.id ?: Uuid.random() },
                                    onItemClicked = {}) { _, deck ->
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = deck.name,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = deck.description ?: "unknown",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = deck.authors ?: "unknown",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
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
