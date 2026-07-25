package one.felsen.auraquiz.data.note

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,
    val body: String,
    val createdAtEpochMillis: Long,
    val isSynced: Boolean = false, // useful once you add a remote backend later
)