package one.felsen.auraquiz.data.deck

import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

class DeckRepository(
    private val deckDao: DeckDao
) {
    suspend fun insertDeck(deck: DeckEntity) = deckDao.insertDeck(deck)
    suspend fun getDeckById(id: Uuid) = deckDao.getDeckById(id)
    suspend fun updateDeck(document: DeckEntity) = deckDao.updateDeck(document)
    suspend fun updateDeckWithTimestamp(deck: DeckEntity) {
        updateDeck(deck.copy(updatedTimestamp = System.currentTimeMillis()))
    }
    fun getAllDecks(): Flow<List<DeckEntity>> = deckDao.getAllDecks()

    suspend fun deleteDeck(deck: DeckEntity) = deckDao.deleteDeck(deck)
}