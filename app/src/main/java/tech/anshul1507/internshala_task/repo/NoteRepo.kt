package tech.anshul1507.internshala_task.repo

import android.app.Application
import androidx.lifecycle.LiveData
import tech.anshul1507.internshala_task.dao.NoteDAO
import tech.anshul1507.internshala_task.db.NoteDB
import tech.anshul1507.internshala_task.entity.NoteModel

class NoteRepo(application: Application) {

    var noteDao = NoteDB.getDatabase(application).noteDao()

    fun getAllNotes(mailId: String): LiveData<List<NoteModel>> = noteDao.getAllNotes(mailId)

    suspend fun insert(note: NoteModel) {
        noteDao.insert(note)
    }

    suspend fun update(note: NoteModel) {
        noteDao.update(note)
    }

    suspend fun delete(note: NoteModel) {
        noteDao.delete(note)
    }

}