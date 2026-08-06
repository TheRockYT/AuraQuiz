package one.felsen.auraquiz.data

import androidx.room3.ColumnTypeConverter
import kotlinx.serialization.json.Json
import one.felsen.auraquiz.data.card.CardData
import kotlin.uuid.Uuid

class Converters {

    private val json = Json { ignoreUnknownKeys = true }

    @ColumnTypeConverter
    fun fromUuid(uuid: Uuid?): String? {
        return uuid?.toString()
    }

    @ColumnTypeConverter
    fun toUuid(uuidString: String?): Uuid? {
        return uuidString?.let { Uuid.parse(it) }
    }


    @ColumnTypeConverter
    fun fromCardData(cardData: CardData): String {
        return json.encodeToString(cardData)
    }

    @ColumnTypeConverter
    fun toCardData(data: String): CardData {
        return json.decodeFromString(data)
    }
}