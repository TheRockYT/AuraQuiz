package one.felsen.auraquiz.data.card

import kotlinx.coroutines.flow.Flow
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
}
