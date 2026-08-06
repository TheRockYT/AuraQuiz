package one.felsen.auraquiz.data.card

import androidx.room3.*
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface CardDao {


    @Query(
        """
            SELECT * FROM cards
            WHERE deckId = :deckId
            ORDER BY updatedTimestamp DESC
        """
    )
    fun getCards(deckId: Uuid): Flow<List<CardEntity>>

    @Upsert
    suspend fun upsertCard(card: CardEntity)

    @Upsert
    suspend fun upsertCardData(cardData: CardDataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviewLog(log: ReviewLogEntity)

    // Get Num Due Cards per Deck
    // Get Num New Cards per Deck
}
