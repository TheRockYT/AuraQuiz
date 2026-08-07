package one.felsen.auraquiz.data

import kotlinx.serialization.Serializable
import one.felsen.auraquiz.data.card.CardData
import kotlin.uuid.Uuid

@Serializable
data class ExportFile(
    val exportTimestamp: Long? = null,
    val decks: List<ExportDeck>? = null
)

@Serializable
data class ExportDeck(
    val id: Uuid? = null,
    val name: String,
    val description: String? = null,
    val authors: String? = null,
    val active: Boolean? = null,
    val creationTimestamp: Long? = null,
    val updatedTimestamp: Long? = null,
    val cards: List<ExportCard>? = null
)

@Serializable
data class ExportCard(
    val id: Uuid? = null,
    val priority: Int? = null,
    val title: String? = null,
    val hint: String? = null,
    val explanation: String? = null,
    val data: CardData,
    val active: Boolean? = null,
    val creationTimestamp: Long? = null,
    val updatedTimestamp: Long? = null,
    val userData: ExportCardUserData? = null,
)

@Serializable
data class ExportCardUserData(
    var dueDate: Long,
    var lastReview: Long,
    val difficulty: Double,
    val stability: Double,
    val creationTimestamp: Long,
    val updatedTimestamp: Long,
)
