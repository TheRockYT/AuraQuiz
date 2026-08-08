package one.felsen.auraquiz.data.card

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import one.felsen.auraquiz.data.deck.DeckEntity
import kotlin.uuid.Uuid

@Entity(tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("deckId")]
)
data class CardEntity(
    @PrimaryKey
    val id: Uuid = Uuid.random(),
    val deckId: Uuid,
    val priority: Int = 0,
    val title: String = "",
    val hint: String = "",
    val explanation: String = "",
    val data: CardData,
    val active: Boolean = true,
    val creationTimestamp: Long = System.currentTimeMillis(),
    val updatedTimestamp: Long = System.currentTimeMillis(),
)