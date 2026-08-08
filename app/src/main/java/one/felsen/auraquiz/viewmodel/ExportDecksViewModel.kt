package one.felsen.auraquiz.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import one.felsen.auraquiz.data.ExportCard
import one.felsen.auraquiz.data.ExportCardUserData
import one.felsen.auraquiz.data.ExportDeck
import one.felsen.auraquiz.data.ExportFile
import one.felsen.auraquiz.data.card.CardRepository
import one.felsen.auraquiz.data.deck.DeckEntity
import one.felsen.auraquiz.data.deck.DeckRepository
import one.felsen.auraquiz.ui.UiState

private val json = Json { prettyPrint = true }

class ExportDecksViewModel(private val deckRepository: DeckRepository, private val cardRepository: CardRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Boolean>>(UiState.Success(false))
    val uiState: StateFlow<UiState<Boolean>> = _uiState.asStateFlow()

    @OptIn(ExperimentalSerializationApi::class)
    fun exportDecks(context: Context, uri: Uri, exportUserData: Boolean, decks: List<DeckEntity>) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val deckList = mutableListOf<ExportDeck>()
            
            for (deck in decks) {

                val cardList = mutableListOf<ExportCard>()
                val cards = cardRepository.getCardsWithDataForDeck(deck.id)
                for ((card, cardData) in cards) {
                    val data = if (exportUserData && cardData != null) {
                        ExportCardUserData(
                            dueDate = cardData.dueDate,
                            lastReview = cardData.lastReview,
                            difficulty = cardData.difficulty,
                            stability = cardData.stability,
                            creationTimestamp = cardData.creationTimestamp,
                            updatedTimestamp = cardData.updatedTimestamp
                        )
                    } else {
                        null
                    }

                    cardList.add(ExportCard(
                        id = card.id,
                        priority = card.priority,
                        title = card.title,
                        hint = card.hint,
                        explanation = card.explanation,
                        data = card.data,
                        active = card.active,
                        creationTimestamp = card.creationTimestamp,
                        updatedTimestamp = card.updatedTimestamp,
                        userData = data
                    ))
                }

                deckList.add(ExportDeck(
                    id = deck.id,
                    name = deck.name,
                    description = deck.description,
                    authors = deck.authors,
                    active = deck.active,
                    creationTimestamp = deck.creationTimestamp,
                    updatedTimestamp = deck.updatedTimestamp,
                    cards = cardList
                ))

            }
            
            val exportDeck = ExportFile(
                exportTimestamp = System.currentTimeMillis(),
                decks = deckList
            )
            
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->

                        json.encodeToStream(exportDeck, outputStream)
                    } ?: throw Exception("Failed to open output stream.")
                }
            }

            result.fold(
                onSuccess = {
                    _uiState.value = UiState.Success(true)
                },
                onFailure = { throwable ->
                    _uiState.value = UiState.Error(throwable.localizedMessage ?: "Unknown error")
                }
            )
        }
    }
}