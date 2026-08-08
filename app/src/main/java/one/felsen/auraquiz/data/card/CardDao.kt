package one.felsen.auraquiz.data.card

import androidx.room3.*
import kotlinx.coroutines.flow.Flow
import one.felsen.auraquiz.data.deck.DeckEntity
import kotlin.uuid.Uuid

@Dao
interface CardDao {


    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCardById(id: Uuid): CardEntity?

    @Query(
        """
            SELECT * FROM cards
            WHERE deckId = :deckId
            ORDER BY updatedTimestamp DESC
        """
    )
    fun getCards(deckId: Uuid): Flow<List<CardEntity>>

    @Insert
    suspend fun insertCard(card: CardEntity)

    @Update
    suspend fun updateCard(card: CardEntity)

    @Delete
    suspend fun deleteCard(card: CardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviewLog(log: ReviewLogEntity)

    // Get Num Due Cards per Deck
    // Get Num New Cards per Deck

    @Transaction
    @Query("SELECT * FROM cards WHERE deckId = :deckId")
    suspend fun getCardsWithDataForDeck(deckId: Uuid): List<CardWithData>



    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCardsAllIgnore(cards: List<CardEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCardDataAllIgnore(cardDat: List<CardDataEntity>): List<Long>

    @Query("""
        UPDATE cards 
        SET deckId = :deckId, priority = :priority, title = :title, hint = :hint, explanation = :explanation, data = :data, active = :active, creationTimestamp = :creationTimestamp, updatedTimestamp = :updatedTimestamp
        WHERE id = :id AND updatedTimestamp < :updatedTimestamp
    """)
    suspend fun updateCardIfNewer(id: Uuid, deckId: Uuid, priority: Int, title: String, hint: String, explanation: String, data: CardData, active: Boolean, creationTimestamp: Long, updatedTimestamp: Long)

    @Query("""
        UPDATE card_data 
        SET dueDate = :dueDate, lastReview = :lastReview, difficulty = :difficulty, stability = :stability, creationTimestamp = :creationTimestamp, updatedTimestamp = :updatedTimestamp
        WHERE id = :id AND updatedTimestamp < :updatedTimestamp
    """)
    suspend fun updateCardDataIfNewer(id: Uuid, dueDate: Long, lastReview: Long, difficulty: Double, stability: Double, creationTimestamp: Long, updatedTimestamp: Long)

    @Upsert
    suspend fun upsertAllCards(cards: List<CardEntity>): List<Long>

    @Upsert
    suspend fun upsertAllCardData(cardData: List<CardDataEntity>): List<Long>

    @Transaction
    suspend fun upsertAllCardsIfNewer(cards: List<CardEntity>) {
        val insertResults = insertCardsAllIgnore(cards)

        for (i in cards.indices) {
            // Room returns -1 for any row that was ignored due to primary key conflict
            if (insertResults[i] == -1L) {
                val card = cards[i]
                updateCardIfNewer(
                    id = card.id,
                    deckId = card.deckId,
                    priority = card.priority,
                    title = card.title,
                    hint = card.hint,
                    explanation = card.explanation,
                    data = card.data,
                    active = card.active,
                    creationTimestamp = card.creationTimestamp,
                    updatedTimestamp = card.updatedTimestamp
                )
            }
        }
    }

    @Transaction
    suspend fun upsertAllCardDataIfNewer(cardData: List<CardDataEntity>) {
        val insertResults = insertCardDataAllIgnore(cardData)

        for (i in cardData.indices) {
            // Room returns -1 for any row that was ignored due to primary key conflict
            if (insertResults[i] == -1L) {
                val cardDataEntity = cardData[i]
                updateCardDataIfNewer(
                    id = cardDataEntity.id,
                    dueDate = cardDataEntity.dueDate,
                    lastReview = cardDataEntity.lastReview,
                    difficulty = cardDataEntity.difficulty,
                    stability = cardDataEntity.stability,
                    creationTimestamp = cardDataEntity.creationTimestamp,
                    updatedTimestamp = cardDataEntity.updatedTimestamp
                )
            }
        }
    }
}
