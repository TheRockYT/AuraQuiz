package one.felsen.auraquiz.ui.screen.deck

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import one.felsen.auraquiz.data.deck.DeckRepository
import one.felsen.auraquiz.ui.UiState
import one.felsen.auraquiz.ui.screen.diolog.ErrorDialog
import one.felsen.auraquiz.ui.screen.diolog.LoadingDialog
import one.felsen.auraquiz.viewmodel.DeckUpsertState
import one.felsen.auraquiz.viewmodel.DeckUpsertViewModel
import kotlin.uuid.Uuid

@Composable
fun DeckUpsertScreen(
    uuid: Uuid?,
    deckRepository: DeckRepository,
    onBack: () -> Unit,
    onDelete: (uuid: Uuid) -> Unit,
    navigateToDeck: (uuid: Uuid) -> Unit
) {

    val deckUpsertViewModel = viewModel { DeckUpsertViewModel(deckRepository, uuid) }

    val uiState by deckUpsertViewModel.uiState.collectAsStateWithLifecycle()

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
            when (val deckUpsertState = state.data) {
                is DeckUpsertState.SAVED -> {
                    LaunchedEffect(deckUpsertState.uuid) {
                        navigateToDeck(deckUpsertState.uuid)
                    }
                }

                is DeckUpsertState.CREATING -> {
                    DeckEditorScreen(
                        isEditMode = false,
                        onBack = onBack,
                        onSave = { name, description, authors, _ ->
                            deckUpsertViewModel.createDeck(name, description, authors)
                        },
                        onDelete = {
                            deckUpsertViewModel.deleteDeck()
                        }
                    )
                }

                is DeckUpsertState.EDITING -> {
                    val initialDeck = deckUpsertState.initialDeck
                    DeckEditorScreen(
                        isEditMode = true,
                        initialName = initialDeck.name,
                        initialDescription = initialDeck.description,
                        initialAuthors = initialDeck.authors,
                        onBack = onBack,
                        onSave = { name, description, authors, _ ->
                            deckUpsertViewModel.updateDeck(name, description, authors)
                        },
                        onDelete = {
                            deckUpsertViewModel.deleteDeck()
                        }
                    )
                }

                is DeckUpsertState.DELETED -> {
                    LaunchedEffect(deckUpsertState.uuid) {
                        onDelete(deckUpsertState.uuid)
                    }
                }
            }
        }
    }

}