package one.felsen.auraquiz.data.note

class NoteRepository(
    private val noteDao: NoteDao
) {
    fun observeAll() = noteDao.observeAll()
    suspend fun getById(id: Long) = noteDao.getById(id)
    suspend fun upsert(note: NoteEntity) = noteDao.upsert(note)
    suspend fun insertAll(notes: List<NoteEntity>) = noteDao.insertAll(notes)
    suspend fun delete(note: NoteEntity) = noteDao.delete(note)
}