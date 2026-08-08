package one.felsen.auraquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import one.felsen.auraquiz.data.card.CardEntity
import one.felsen.auraquiz.data.card.CardRepository
import one.felsen.auraquiz.ui.UiState

class QuizViewModel(private val cardRepository: CardRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow<UiState<CardEntity>>(UiState.Loading)
    val uiState: StateFlow<UiState<CardEntity>> = _uiState.asStateFlow()


    init {
        nextCard()
    }

    fun nextCard() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val nextCard = cardRepository.getNextCardToStudy()
                if (nextCard != null) {
                    _uiState.value = UiState.Success(nextCard.card)
                } else {
                    _uiState.value = UiState.Error("No cards available for study.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }
}
