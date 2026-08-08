package one.felsen.auraquiz.data.card

import android.os.Build
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlin.time.Duration.Companion.days
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

    suspend fun insertCardDataAllIgnore(cardData: List<CardDataEntity>): List<Long> =
        cardDao.insertCardDataAllIgnore(cardData)

    suspend fun upsertAllCardData(cardData: List<CardDataEntity>): List<Long> = cardDao.upsertAllCardData(cardData)
    suspend fun upsertAllCardDataIfNewer(cardData: List<CardDataEntity>) = cardDao.upsertAllCardDataIfNewer(cardData)

    suspend fun getNextCardToStudy(settingsMax: Int): CardWithData? {
        val now = System.currentTimeMillis()

        // Try to get a due card from an active deck first
        val dueCard = cardDao.getNextDueCard(currentTimestamp = now)
        if (dueCard != null) {
            return dueCard
        }

        val fourAmTimestamp: Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
                .atTime(LocalTime.of(4, 0))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        } else {
            now.minus(1.days.inWholeMilliseconds)
        }

        val candidates = mutableListOf<CardWithData>()

        val decks = cardDao.getAllDecks()
        for ((id) in decks) {
            val newlyStudiedToday = cardDao.getNewCardsStudiedCountSinceOnDeck(fourAmTimestamp, id)

            if (newlyStudiedToday < settingsMax) {
                // We are under the limit, fetch a fresh card
                cardDao.getNextNewCard(id)?.let { candidates.add(it) }
            }
        }

        candidates.sortBy { it.card.priority }

        return candidates.firstOrNull()
    }

    suspend fun upsertCardData(cardDataEntity: CardDataEntity) = cardDao.upsertCardData(cardDataEntity)

}
