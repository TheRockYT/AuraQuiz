package one.felsen.auraquiz.ui.screen.deck

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import one.felsen.auraquiz.data.card.CardRepository
import one.felsen.auraquiz.ui.UiState
import one.felsen.auraquiz.ui.screen.dialog.ErrorDialog
import one.felsen.auraquiz.ui.screen.dialog.LoadingDialog
import one.felsen.auraquiz.ui.screen.settings.SettingsPlane
import one.felsen.auraquiz.ui.screen.settings.components.ListComponent
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
                    ListComponent(
                        items = cards,
                        indexId = { it.id },
                        onItemClicked = { card ->
                            onSelectCard(card.id)
                        }
                    ) { index, card ->
                        Row {
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
