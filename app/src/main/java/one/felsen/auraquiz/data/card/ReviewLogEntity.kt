package one.felsen.auraquiz.data.card

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "review_logs",
    foreignKeys = [
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cardId")] // deckId removed here to save space, we can join if needed
)
data class ReviewLogEntity(
    @PrimaryKey
    val id: Uuid = Uuid.random(),
    val cardId: Uuid,
    val reviewedAt: Long,
    val rating: Int,
    val elapsedDays: Double
)
