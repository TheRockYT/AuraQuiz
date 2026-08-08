package one.felsen.auraquiz.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
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
import kotlinx.serialization.json.decodeFromStream
import one.felsen.auraquiz.data.ExportFile
import one.felsen.auraquiz.data.card.CardDataEntity
import one.felsen.auraquiz.data.card.CardEntity
import one.felsen.auraquiz.data.card.CardRepository
import one.felsen.auraquiz.data.deck.DeckEntity
import one.felsen.auraquiz.data.deck.DeckRepository
import one.felsen.auraquiz.ui.UiState

private val json = Json { prettyPrint = true }

class ImportDecksViewModel(
    private val uri: Uri,
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<ImportDecksState>>(UiState.Loading)
    val uiState: StateFlow<UiState<ImportDecksState>> = _uiState.asStateFlow()

    @OptIn(ExperimentalSerializationApi::class)
    fun loadFile(context: Context) {
        viewModelScope.launch {

            val result = withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openInputStream(uri)?.use { outputStream ->

                        json.decodeFromStream<ExportFile>(outputStream)
                    } ?: throw Exception("Failed to open output stream.")
                }
            }

            result.fold(
                onSuccess = {
                    _uiState.value = UiState.Success(ImportDecksState.IMPORTING(it))
                },
                onFailure = { throwable ->
                    _uiState.value = UiState.Error(throwable.localizedMessage ?: "Unknown error")
                }
            )
        }
    }

    fun import(selectedStrategy: ConflictStrategy, importUserData: Boolean) {
        viewModelScope.launch {
            val stateValue = _uiState.value
            if (stateValue !is UiState.Success) {
                _uiState.value = UiState.Error("Cannot import decks: not in a success state")
                return@launch
            }

            val data = stateValue.data

            if (stateValue.data !is ImportDecksState.IMPORTING) {
                _uiState.value = UiState.Error("Cannot import decks: not in an importing state")
                return@launch
            }


            _uiState.value = UiState.Loading

            val importingDecks = mutableListOf<DeckEntity>()
            val importingCards = mutableListOf<CardEntity>()
            val importingCardUserData = mutableListOf<CardDataEntity>()

            data.file.decks?.forEach { deck ->
                val defaultEntity = DeckEntity()
                val deckId = deck.id ?: defaultEntity.id
                val deckEntity = defaultEntity.copy(
                    id = deckId,
                    name = deck.name,
                    description = deck.description ?: defaultEntity.description,
                    authors = deck.authors ?: defaultEntity.authors,
                    active = deck.active ?: defaultEntity.active,
                    creationTimestamp = deck.creationTimestamp ?: defaultEntity.creationTimestamp,
                    updatedTimestamp = deck.updatedTimestamp ?: defaultEntity.updatedTimestamp
                )
                importingDecks.add(deckEntity)
                deck.cards?.forEach { card ->
                    val userData = card.userData
                    val defaultCard = CardEntity(
                        deckId = deckId,
                        data = card.data
                    )
                    val cardId = card.id ?: defaultCard.id
                    val cardEntity = defaultCard.copy(
                        id = cardId,
                        priority = card.priority ?: defaultCard.priority,
                        title = card.title ?: defaultCard.title,
                        hint = card.hint ?: defaultCard.hint,
                        explanation = card.explanation ?: defaultCard.explanation,
                        active = card.active ?: defaultCard.active,
                        creationTimestamp = card.creationTimestamp ?: defaultCard.creationTimestamp,
                        updatedTimestamp = card.updatedTimestamp ?: defaultCard.updatedTimestamp
                    )
                    importingCards.add(cardEntity)
                    if (userData != null) {
                        val cardData = CardDataEntity(
                            id = cardId,
                            dueDate = userData.dueDate,
                            lastReview = userData.lastReview,
                            difficulty = userData.stability,
                            stability = userData.stability,
                            creationTimestamp = userData.creationTimestamp,
                            updatedTimestamp = userData.updatedTimestamp
                        )
                        importingCardUserData.add(cardData)
                    }
                }
            }


            try {

                when (selectedStrategy) {
                    ConflictStrategy.MERGE -> {
                        deckRepository.upsertAllDeckIfNewer(importingDecks)
                        cardRepository.upsertAllCardsIfNewer(importingCards)

                        if (importUserData) {
                            cardRepository.upsertAllCardDataIfNewer(importingCardUserData)
                        }
                    }

                    ConflictStrategy.PREFER_IMPORTED -> {
                        deckRepository.upsertAllDecks(importingDecks)
                        cardRepository.upsertAllCards(importingCards)

                        if (importUserData) {
                            cardRepository.upsertAllCardData(importingCardUserData)
                        }
                    }

                    ConflictStrategy.PREFER_LOCAL -> {
                        deckRepository.insertDeckAllIgnore(importingDecks)
                        cardRepository.insertCardsAllIgnore(importingCards)

                        if (importUserData) {
                            cardRepository.insertCardDataAllIgnore(importingCardUserData)
                        }
                    }
                }

                _uiState.value = UiState.Success(ImportDecksState.COMPLETED)

            } catch (e: Exception) {
                Log.d("CardUpsertViewModel", "Creating card with error: ${e.message}")
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }
}