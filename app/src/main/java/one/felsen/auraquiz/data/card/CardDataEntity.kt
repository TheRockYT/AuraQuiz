package one.felsen.auraquiz.data.card

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(
    tableName = "card_data",
    foreignKeys = [
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CardDataEntity(
    @PrimaryKey
    val id: Uuid,
    var dueDate: Long,
    var lastReview: Long,
    val difficulty: Double,
    val stability: Double,
    val creationTimestamp: Long = System.currentTimeMillis(),
    val updatedTimestamp: Long = System.currentTimeMillis(),
)