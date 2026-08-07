package one.felsen.auraquiz.ui.screen.deck.card

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import one.felsen.auraquiz.data.card.CardRepository
import one.felsen.auraquiz.ui.UiState
import one.felsen.auraquiz.ui.screen.diolog.ErrorDialog
import one.felsen.auraquiz.ui.screen.diolog.LoadingDialog
import one.felsen.auraquiz.viewmodel.CardUpsertState
import one.felsen.auraquiz.viewmodel.CardUpsertViewModel
import kotlin.uuid.Uuid

@Composable
fun CardScreen(
    onBack: () -> Unit,
    cardRepository: CardRepository,
    deckId: Uuid,
    cardId: Uuid? = null
) {
    val cardUpsertViewModel = viewModel { CardUpsertViewModel(cardRepository, deckId, cardId) }

    val uiState by cardUpsertViewModel.uiState.collectAsStateWithLifecycle()

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
                is CardUpsertState.SAVED -> {
                    LaunchedEffect(deckUpsertState.uuid) {
                        onBack()
                    }
                }

                is CardUpsertState.CREATING -> {
                    CardEditorScreen(
                        isEditing = false,
                        onBack = onBack,
                        onSave = { title, hint, explanation, cardData ->
                            cardUpsertViewModel.upsertCard(title, hint, explanation, cardData)
                        },
                        onDelete = {
//                            cardUpsertViewModel.deleteCard()
                        }
                    )
                }

                is CardUpsertState.EDITING -> {
                    CardEditorScreen(
                        isEditing = true,
                        onBack = onBack,
                        onSave = { title, hint, explanation, cardData ->
                            cardUpsertViewModel.upsertCard(title, hint, explanation, cardData)
                        },
                        onDelete = {
//                            cardUpsertViewModel.deleteCard()
                        }
                    )
                }

                is CardUpsertState.DELETED -> {

                }
            }
        }
    }
}
