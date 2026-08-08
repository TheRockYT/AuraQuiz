package one.felsen.auraquiz.data.card

import kotlinx.coroutines.flow.Flow
import one.felsen.auraquiz.data.deck.DeckEntity
import kotlin.uuid.Uuid

class CardRepository(
    private val cardDao: CardDao
) {

    fun getCards(deckId: Uuid): Flow<List<CardEntity>> = cardDao.getCards(deckId)
    suspend fun insertCard(card: CardEntity) = cardDao.insertCard(card)

    suspend fun getCardById(id: Uuid) = cardDao.getCardById(id)
    suspend fun updateCard(card: CardEntity) = cardDao.updateCard(card)
    suspend fun deleteCard(card: CardEntity) = cardDao.deleteCard(card)
    suspend fun updateCardWithTimestamp(card: CardEntity) {
        updateCard(card.copy(updatedTimestamp = System.currentTimeMillis()))
    }
    suspend fun getCardsWithDataForDeck(deckId: Uuid) = cardDao.getCardsWithDataForDeck(deckId)

    suspend fun insertCardsAllIgnore(cards: List<CardEntity>): List<Long> = cardDao.insertCardsAllIgnore(cards)
    suspend fun upsertAllCards(cards: List<CardEntity>): List<Long> = cardDao.upsertAllCards(cards)
    suspend fun upsertAllCardsIfNewer(cards: List<CardEntity>) = cardDao.upsertAllCardsIfNewer(cards)

    suspend fun insertCardDataAllIgnore(cardData: List<CardDataEntity>): List<Long> = cardDao.insertCardDataAllIgnore(cardData)
    suspend fun upsertAllCardData(cardData: List<CardDataEntity>): List<Long> = cardDao.upsertAllCardData(cardData)
    suspend fun upsertAllCardDataIfNewer(cardData: List<CardDataEntity>) = cardDao.upsertAllCardDataIfNewer(cardData)

}
