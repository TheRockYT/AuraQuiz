package one.felsen.auraquiz.data.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.time.Clock

class NotesViewModel(private val repository: NoteRepository) : ViewModel() {
    val tasks = repository.observeAll()

    fun addTask(title: String) {
        viewModelScope.launch {
            repository.upsert(
                NoteEntity(
                    title = title,
                    body = "Test",
                    createdAtEpochMillis = Clock.System.now().toEpochMilliseconds(),
                )
            )
        }
    }
}