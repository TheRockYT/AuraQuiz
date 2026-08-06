package one.felsen.auraquiz.data.card

import androidx.room3.Embedded

data class CardWithData(
    @Embedded val card: CardEntity,
    @Embedded val cardData: CardDataEntity?
)
