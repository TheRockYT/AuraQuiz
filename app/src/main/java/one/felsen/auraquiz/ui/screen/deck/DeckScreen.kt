package one.felsen.auraquiz.ui.screen.deck

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import one.felsen.auraquiz.data.card.CardRepository
import one.felsen.auraquiz.ui.UiState
import one.felsen.auraquiz.ui.screen.diolog.ErrorDialog
import one.felsen.auraquiz.ui.screen.diolog.LoadingDialog
import one.felsen.auraquiz.ui.screen.settings.SettingsPlane
import one.felsen.auraquiz.viewmodel.DeckDetailsViewModel
import kotlin.uuid.Uuid

@Composable
fun DeckScreen(
    onBack: () -> Unit,
    deckId: Uuid,
    onEditDeckClick: () -> Unit,
    onSelectCard: (Uuid) -> Unit,
    onSelectCreateCard: () -> Unit,
    cardRepository: CardRepository
) {

    val deckDetailsViewModel = viewModel { DeckDetailsViewModel(cardRepository, deckId) }
    val uiState by deckDetailsViewModel.uiState.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    val fabVisible by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }


    SettingsPlane(
        title = "Deck", onBack = onBack,
        actions = {
            IconButton(onClick = { onEditDeckClick() }) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Deck")
            }
        },

        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.animateFloatingActionButton(
                    visible = fabVisible,
                    alignment = Alignment.BottomEnd
                ),
                onClick = {
                    onSelectCreateCard()
                }
            ) {
                Icon(
                    painter = rememberVectorPainter(Icons.Filled.Add),
                    contentDescription = "Add"
                )
            }
        }) {

        when (val state = uiState) {
            is UiState.Error -> {
                ErrorDialog(state.message, onBack)
            }

            is UiState.Loading -> {
                LoadingDialog(onDismissRequest = onBack)
            }

            is UiState.Success -> {
                val cards = state.data
                if (cards.isEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("No Cards Yet")
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Get Started by creating or importing a card",
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
                            items = cards,
                            key = { _, card -> card.id }
                        ) { index, card ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                onClick = {
                                    onSelectCard(card.id)
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {

                                    // Deck Title and Description
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = card.title,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .basicMarquee(),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Text(
                                            text = card.explanation,
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
    }
}
