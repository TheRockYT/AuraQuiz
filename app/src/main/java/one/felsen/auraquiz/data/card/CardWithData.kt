package one.felsen.auraquiz.data.card

import androidx.room3.Embedded
import androidx.room3.Relation

data class CardWithData(
    @Embedded
    val card: CardEntity,

    @Relation(parentColumns = ["id"], entityColumns = ["id"])
    val cardData: CardDataEntity?
)
