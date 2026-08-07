package one.felsen.auraquiz.data.card

import androidx.room3.*
import kotlinx.coroutines.flow.Flow
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
}
