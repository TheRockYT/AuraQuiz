package one.felsen.auraquiz.ui.screen.settings

import android.net.Uri
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import one.felsen.auraquiz.data.deck.DeckRepository
import one.felsen.auraquiz.ui.UiState
import one.felsen.auraquiz.ui.screen.diolog.ErrorDialog
import one.felsen.auraquiz.ui.screen.diolog.LoadingDialog
import one.felsen.auraquiz.viewmodel.DecksViewModel
import one.felsen.auraquiz.ui.screen.settings.components.FilePickerScreen
import kotlin.uuid.Uuid

@Composable
fun SettingsDecks(
    onBack: () -> Unit,
    onSelectDeck: (Uuid) -> Unit,
    onSelectImport: (Uri) -> Unit,
    onSelectCreate: () -> Unit,
    deckRepository: DeckRepository
) {
    val decksViewModel = viewModel { DecksViewModel(deckRepository) }
    val uiState by decksViewModel.uiState.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    val fabVisible by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }

    val items = listOf(
        Icons.Filled.Add to "Create",
        Icons.Filled.Upload to "Import",
    )

    var showUploadDialog by remember { mutableStateOf(false) }

    SettingsPlane(
        title = "Decks", onBack = onBack, floatingActionButton = {
            FloatingActionButtonMenu(
                expanded = fabMenuExpanded, button = {
                    ToggleFloatingActionButton(
                        modifier = Modifier.animateFloatingActionButton(
                            visible = fabVisible || fabMenuExpanded, alignment = Alignment.BottomEnd
                        ), checked = fabMenuExpanded, onCheckedChange = { fabMenuExpanded = !fabMenuExpanded }) {
                        val imageVector by remember {
                            derivedStateOf {
                                if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                            }
                        }
                        Icon(
                            painter = rememberVectorPainter(imageVector),
                            contentDescription = null,
                            modifier = Modifier.animateIcon({ checkedProgress })
                        )
                    }
                }) {
                items.forEachIndexed { _, item ->
                    FloatingActionButtonMenuItem(
                        onClick = {
                            fabMenuExpanded = false

                            when (item.second) {
                                "Create" -> onSelectCreate()
                                "Import" -> showUploadDialog = true
                            }
                        },
                        icon = { Icon(item.first, contentDescription = null) },
                        text = { Text(text = item.second) },
                    )
                }
            }
        }) {

        when (val state = uiState) {
            is UiState.Error -> {
                ErrorDialog(
                    message = state.message,
                    onDismissRequest = { onBack() }
                )
            }

            is UiState.Loading -> {
                LoadingDialog()
            }

            is UiState.Success -> {
                val decks = state.data
                if (decks.isEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                            ) {
                                Text("No Decks Yet")
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Get Started by creating or importing a deck"
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(
                            items = decks,
                            key = { _, deck -> deck.id } // Replace deck.id with a unique identifier if available
                        ) { index, deck ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                onClick = {
                                    onSelectDeck(deck.id)
                                }) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }

                                    // Deck Title and Description
                                    Column(
                                        modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = deck.name,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .basicMarquee(),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Text(
                                            text = deck.description,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .basicMarquee(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }



        if (showUploadDialog) {
            FilePickerScreen(
                title = "Select a file",
                description = "Select a deck file to continue",
                onFileSelected = { url ->
                    onSelectImport(url)
                    showUploadDialog = false
                },
                onDismiss = {
                    showUploadDialog = false
                })
        }
    }
}
