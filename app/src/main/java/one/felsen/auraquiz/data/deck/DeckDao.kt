package one.felsen.auraquiz.data.deck

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface DeckDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: DeckEntity)

    @Query("SELECT * FROM decks WHERE id = :id")
    suspend fun getDeckById(id: Uuid): DeckEntity?

    @Update
    suspend fun updateDeck(document: DeckEntity)

    @Query("SELECT * FROM decks ORDER BY updatedTimestamp DESC")
    fun getAllDecks(): Flow<List<DeckEntity>>
}