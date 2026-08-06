package one.felsen.auraquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import one.felsen.auraquiz.data.deck.DeckEntity
import one.felsen.auraquiz.data.deck.DeckRepository
import one.felsen.auraquiz.ui.UiState

class DecksViewModel(repository: DeckRepository) : ViewModel() {
    val uiState: StateFlow<UiState<List<DeckEntity>>> = repository.getAllDecks()
        .map<List<DeckEntity>, UiState<List<DeckEntity>>> { decks ->
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
