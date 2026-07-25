package one.felsen.auraquiz.data

import androidx.room3.ColumnTypeConverter
import java.util.UUID
import kotlin.uuid.Uuid

class Converters {
    // UUID Converters
    @ColumnTypeConverter
    fun fromUuid(uuid: Uuid?): String? {
        return uuid?.toString()
    }

    @ColumnTypeConverter
    fun toUuid(uuidString: String?): Uuid? {
        return uuidString?.let { Uuid.parse(it) }
    }
}