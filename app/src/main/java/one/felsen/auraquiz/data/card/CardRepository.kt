package one.felsen.auraquiz.data.card

import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

class CardRepository(
    private val cardDao: CardDao
) {
    fun getCards(deckId: Uuid): Flow<List<CardEntity>> = cardDao.getCards(deckId)
}
