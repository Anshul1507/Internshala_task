package tech.anshul1507.internshala_task.ui

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.anshul1507.internshala_task.db.NoteDB
import tech.anshul1507.internshala_task.entity.NoteModel
import tech.anshul1507.internshala_task.repo.NoteRepo

class HomeFragmentViewModel(application: Application) : AndroidViewModel(application) {

    var repos: NoteRepo

    init {
        val dao = NoteDB.getDatabase(application).noteDao()
        repos = NoteRepo(application)
    }

    fun insertNode(note: NoteModel) = viewModelScope.launch(Dispatchers.IO) {
        repos.insert(note)
    }

    fun updateNode(note: NoteModel) = viewModelScope.launch(Dispatchers.IO) {
        repos.update(note)
    }

    fun deleteNode(note: NoteModel) = viewModelScope.launch(Dispatchers.IO) {
        repos.delete(note)
    }

    fun getAllNotes(mailID: String): LiveData<List<NoteModel>> {
        return repos.getAllNotes(mailID)
    }
}