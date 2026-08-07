package one.felsen.auraquiz.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import one.felsen.auraquiz.data.card.CardData
import one.felsen.auraquiz.data.card.CardEntity
import one.felsen.auraquiz.data.card.CardRepository
import one.felsen.auraquiz.ui.UiState
import kotlin.uuid.Uuid

class CardUpsertViewModel(
    private val repository: CardRepository,
    private val deckId: Uuid,
    private val cardId: Uuid? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<CardUpsertState>>(UiState.Loading)
    val uiState: StateFlow<UiState<CardUpsertState>> = _uiState.asStateFlow()

    init {
        if (cardId != null) {
            Log.d("CardUpsertViewModel", "Editing card with UUID: $cardId")
            loadCard()
        } else {
            Log.d("CardUpsertViewModel", "Creating new card")
            _uiState.value = UiState.Success(CardUpsertState.CREATING)
        }
    }

    fun loadCard() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            if (cardId == null) {
                Log.d("CardUpsertViewModel", "Card UUID is null")
                _uiState.value = UiState.Error("Card UUID is null")
                return@launch
            }

            val card = repository.getCardById(cardId)

            if (card == null) {
                Log.d("CardUpsertViewModel", "Card not found")
                _uiState.value = UiState.Error("Card not found")
                return@launch
            }

            Log.d("CardUpsertViewModel", "Card found: ${card.title}")
            _uiState.value = UiState.Success(CardUpsertState.EDITING(card))
        }
    }

    fun upsertCard(title: String, hint: String, explanation: String, cardData: CardData) {
        viewModelScope.launch {
            Log.d("CardUpsertViewModel", "Card upsert requested")

            val stateValue = _uiState.value
            if (stateValue !is UiState.Success) {
                Log.d("CardUpsertViewModel", "Cannot upsert card: not in a success state")
                _uiState.value = UiState.Error("Cannot upsert card: not in a success state")
                return@launch
            }

            val data = stateValue.data

            _uiState.value = UiState.Loading

            try {

                when (data) {
                    is CardUpsertState.CREATING -> {
                        Log.d(
                            "CardUpsertViewModel",
                            "Creating card with title: $title, hint: $hint, explanation: $explanation, cardData: $cardData"
                        )
                        val entry = CardEntity(
                            deckId = deckId,
                            priority = 0,
                            title = title,
                            hint = hint,
                            explanation = explanation,
                            data = cardData
                        )

                        repository.insertCard(entry)

                        Log.d("CardUpsertViewModel", "Card successfully created")
                        _uiState.value = UiState.Success(CardUpsertState.SAVED(entry.id))
                    }

                    is CardUpsertState.EDITING -> {
                        Log.d(
                            "CardUpsertViewModel",
                            "Updating card with title: $title, hint: $hint, explanation: $explanation, cardData: $cardData"
                        )

                        val card = data.initialCard
                        val newCard = card.copy(
                            title = title,
                            hint = hint,
                            explanation = explanation,
                            data = cardData
                        )

                        repository.updateCardWithTimestamp(card = newCard)

                        Log.d("CardUpsertViewModel", "Card successfully updated")
                        _uiState.value = UiState.Success(CardUpsertState.SAVED(newCard.id))
                    }

                    else -> {
                        Log.d("CardUpsertViewModel", "Updating card: not in a creating state")
                        _uiState.value = UiState.Error("Cannot update card: not in a creating state")
                    }
                }

            } catch (e: Exception) {
                Log.d("CardUpsertViewModel", "Creating card with error: ${e.message}")
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }
}
