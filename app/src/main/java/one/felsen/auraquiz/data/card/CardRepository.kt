package one.felsen.auraquiz.data.card

import kotlinx.coroutines.flow.Flow
import one.felsen.auraquiz.data.deck.DeckEntity
import java.time.ZoneId
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant
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


    suspend fun getNextCardToStudy(): CardWithData? {
        val now = System.currentTimeMillis()

        // Try to get a due card from an active deck first
        val dueCard = cardDao.getNextDueCard(currentTimestamp = now)
        if (dueCard != null) {
            return dueCard
        }

        // If no due cards, check if we can add a new card
        val startOfDay = now - 1.days.inWholeMilliseconds
        val newlyStudiedToday = cardDao.getNewCardsStudiedCountSince(startOfDay)

        if (newlyStudiedToday < 30) {
            // We are under the limit, fetch a fresh card
            return cardDao.getNextNewCard()
        }

        // No due cards and daily limit reached. The study session is done for now!
        return null
    }

}
