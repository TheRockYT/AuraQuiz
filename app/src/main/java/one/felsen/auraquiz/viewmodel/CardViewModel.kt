package one.felsen.auraquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import one.felsen.auraquiz.data.card.CardEntity
import one.felsen.auraquiz.data.card.CardRepository
import one.felsen.auraquiz.data.deck.DeckEntity
import one.felsen.auraquiz.ui.UiState
import kotlin.uuid.Uuid

class CardViewModel(private val repository: CardRepository, deckId: Uuid) : ViewModel() {
    val uiState: StateFlow<UiState<List<CardEntity>>> = repository.getCards(deckId)
        .map<List<CardEntity>, UiState<List<CardEntity>>> { decks ->
            UiState.Success(decks)
        }
        .onStart {
            emit(UiState.Loading)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )
}
