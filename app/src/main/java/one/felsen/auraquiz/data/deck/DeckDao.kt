package one.felsen.auraquiz.data.deck

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Update
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface DeckDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: DeckEntity)

    @Query("SELECT * FROM decks WHERE id = :id")
    suspend fun getDeckById(id: Uuid): DeckEntity?

    @Update
    suspend fun updateDeck(deck: DeckEntity)

    @Query("SELECT * FROM decks ORDER BY updatedTimestamp DESC")
    fun getAllDecks(): Flow<List<DeckEntity>>

    @Delete
    suspend fun deleteDeck(deck: DeckEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDeckAllIgnore(decks: List<DeckEntity>): List<Long>

    @Query("""
        UPDATE decks 
        SET name = :name, description = :description, authors = :authors, active = :active, creationTimestamp = :creationTimestamp, updatedTimestamp = :updatedTimestamp
        WHERE id = :id AND updatedTimestamp < :updatedTimestamp
    """)
    suspend fun updateDeckIfNewer(id: Uuid, name: String, description: String, authors: String, active: Boolean, creationTimestamp: Long, updatedTimestamp: Long)

    @Upsert
    suspend fun upsertAllDecks(decks: List<DeckEntity>): List<Long>

    @Transaction
    suspend fun upsertAllDeckIfNewer(decks: List<DeckEntity>) {
        val insertResults = insertDeckAllIgnore(decks)

        for (i in decks.indices) {
            // Room returns -1 for any row that was ignored due to primary key conflict
            if (insertResults[i] == -1L) {
                val deck = decks[i]
                updateDeckIfNewer(
                    id = deck.id,
                    name = deck.name,
                    description = deck.description,
                    authors = deck.authors,
                    active = deck.active,
                    creationTimestamp = deck.creationTimestamp,
                    updatedTimestamp = deck.updatedTimestamp
                )
            }
        }
    }
}