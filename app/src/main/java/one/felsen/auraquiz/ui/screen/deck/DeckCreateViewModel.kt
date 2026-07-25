package one.felsen.auraquiz.ui.screen.deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import one.felsen.auraquiz.data.deck.DeckEntity
import one.felsen.auraquiz.data.deck.DeckRepository
import one.felsen.auraquiz.ui.screen.DeckOverviewScreen
import one.felsen.auraquiz.ui.screen.NavEvent
import kotlin.time.Duration.Companion.milliseconds

class DeckViewModel(private val repository: DeckRepository) : ViewModel() {
    val decks = repository.getAllDecks()

    private val _navEvents = Channel<NavEvent>()
    val navEvents = _navEvents.receiveAsFlow()

    private val _creatingDeck = MutableStateFlow(false)
    val creatingDeck: StateFlow<Boolean> = _creatingDeck.asStateFlow()

    fun createDeck(name: String, description: String, authors: String) {
        _creatingDeck.update { true }

        val entry = DeckEntity(
            name = name,
            description = description,
            authors = authors
        )
        viewModelScope.launch {
            delay(2000L.milliseconds)
            repository.insertDeck(
                entry
            )
            _navEvents.send(NavEvent.NavigateTo(DeckOverviewScreen(entry.id)))
            _creatingDeck.update { false }
        }
    }
}
