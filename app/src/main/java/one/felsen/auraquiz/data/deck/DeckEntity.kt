package one.felsen.auraquiz.data.deck

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlin.uuid.Uuid

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey
    val id: Uuid = Uuid.random(),
    val name: String = "",
    val description: String = "",
    val authors: String = "",
    val active: Boolean = true,
    val creationTimestamp: Long = System.currentTimeMillis(),
    val updatedTimestamp: Long = System.currentTimeMillis()
)