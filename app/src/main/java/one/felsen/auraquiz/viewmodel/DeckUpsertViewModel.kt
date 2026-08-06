package one.felsen.auraquiz.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import one.felsen.auraquiz.data.deck.DeckEntity
import one.felsen.auraquiz.data.deck.DeckRepository
import one.felsen.auraquiz.ui.UiState
import kotlin.uuid.Uuid

class DeckUpsertViewModel(private val repository: DeckRepository, private val uuid: Uuid?) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<DeckUpsertState>>(UiState.Loading)
    val uiState: StateFlow<UiState<DeckUpsertState>> = _uiState.asStateFlow()


    init {
        if (uuid != null) {
            Log.d("DeckUpsertViewModel", "Editing deck with UUID: $uuid")
            loadDeck()
        } else {
            Log.d("DeckUpsertViewModel", "Editing deck with no uuid")
            _uiState.value = UiState.Success(DeckUpsertState.CREATING)
        }
    }

    fun loadDeck() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            if (uuid == null) {
                Log.d("DeckUpsertViewModel", "UUID is null")
                _uiState.value = UiState.Error("UUID is null")
                return@launch
            }

            val deck = repository.getDeckById(uuid)

            if (deck == null) {
                Log.d("DeckUpsertViewModel", "Deck not found")
                _uiState.value = UiState.Error("Deck not found")
                return@launch
            }

            Log.d("DeckUpsertViewModel", "Deck found: ${deck.name}")
            _uiState.value = UiState.Success(DeckUpsertState.EDITING(deck))
        }
    }

    fun createDeck(name: String, description: String, authors: String) {
        viewModelScope.launch {
            Log.d("DeckUpsertViewModel", "Deck creation requested")

            val stateValue = _uiState.value
            if (stateValue !is UiState.Success) {
                Log.d("DeckUpsertViewModel", "Cannot create deck: not in a success state")
                _uiState.value = UiState.Error("Cannot create deck: not in a success state")
                return@launch
            }

            val data = stateValue.data
            if (data !is DeckUpsertState.CREATING) {
                Log.d("DeckUpsertViewModel", "Creating deck: not in a creating state")
                _uiState.value = UiState.Error("Cannot create deck: not in a creating state")
                return@launch
            }

            _uiState.value = UiState.Loading

            try {
                Log.d(
                    "DeckUpsertViewModel",
                    "Creating deck with name: $name, description: $description, authors: $authors"
                )
                val entry = DeckEntity(
                    name = name,
                    description = description,
                    authors = authors
                )

                repository.insertDeck(entry)

                Log.d("DeckUpsertViewModel", "Deck successfully created")
                _uiState.value = UiState.Success(DeckUpsertState.SAVED(entry.id))
            } catch (e: Exception) {
                Log.d("DeckUpsertViewModel", "Creating deck with error: ${e.message}")
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }

    fun updateDeck(name: String, description: String, authors: String) {
        viewModelScope.launch {
            Log.d("DeckUpsertViewModel", "Updating deck requested")

            val stateValue = _uiState.value
            if (stateValue !is UiState.Success) {
                Log.d("DeckUpsertViewModel", "Cannot update deck: not in a success state")
                _uiState.value = UiState.Error("Cannot update deck: not in a success state")
                return@launch
            }

            val data = stateValue.data
            if (data !is DeckUpsertState.EDITING) {
                Log.d("DeckUpsertViewModel", "Cannot update deck: not in an editing state")
                _uiState.value = UiState.Error("Cannot update deck: not in an editing state")
                return@launch
            }
            try {
                Log.d(
                    "DeckUpsertViewModel",
                    "Updating deck with name: $name, description: $description, authors: $authors"
                )

                val deck = data.initialDeck
                val newDeck = deck.copy(
                    name = name,
                    description = description,
                    authors = authors,
                )

                repository.updateDeckWithTimestamp(deck = newDeck)

                Log.d("DeckUpsertViewModel", "Deck successfully updated")
                _uiState.value = UiState.Success(DeckUpsertState.SAVED(newDeck.id))
            } catch (e: Exception) {
                Log.d("DeckUpsertViewModel", "Updating deck with error: ${e.message}")
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }

    fun deleteDeck() {
        viewModelScope.launch {
            Log.d("DeckUpsertViewModel", "Deck deletion requested")

            val stateValue = _uiState.value
            if (stateValue !is UiState.Success) {
                Log.d("DeckUpsertViewModel", "Cannot delete deck: not in a success state")
                _uiState.value = UiState.Error("Cannot delete deck: not in a success state")
                return@launch
            }

            val data = stateValue.data
            if (data !is DeckUpsertState.EDITING) {
                Log.d("DeckUpsertViewModel", "Cannot delete deck: not in an editing state")
                _uiState.value = UiState.Error("Cannot delete deck: not in an editing state")
                return@launch
            }

            try {
                Log.d("DeckUpsertViewModel", "Deleting deck with UUID: ${data.initialDeck.id}")

                val deck = data.initialDeck

                repository.deleteDeck(deck)
                _uiState.value = UiState.Success(DeckUpsertState.DELETED(deck.id))

                Log.d("DeckUpsertViewModel", "Deck successfully deleted")
            } catch (e: Exception) {
                Log.d("DeckUpsertViewModel", "Delete deck with error: ${e.message}")
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }
}
