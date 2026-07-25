package one.felsen.auraquiz.data

import android.content.Context
import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import one.felsen.auraquiz.data.deck.DeckDao
import one.felsen.auraquiz.data.deck.DeckEntity
import one.felsen.auraquiz.data.note.NoteDao
import one.felsen.auraquiz.data.note.NoteEntity

@Database(entities = [NoteEntity::class, DeckEntity::class], version = 1)
@ColumnTypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    abstract fun deckDao(): DeckDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "data.db"
            ).build()
        }
    }
}
