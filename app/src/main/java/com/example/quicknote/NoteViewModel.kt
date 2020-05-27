package com.example.quicknote

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.quicknote.database.NoteRoomDatabase
import com.example.quicknote.model.Note
import com.example.quicknote.model.NoteDeleted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Class that extends the AndroidViewModel class.
 *  All the methods uses een viewModelScope so that do the job in de background.
 */
class NoteViewModel(application: Application) : AndroidViewModel(application) {

    // Variables
    private var repository: NoteRepository
    var allNotes: LiveData<MutableList<Note>>
    var allDeletedNotes: LiveData<MutableList<Note>>

    init {
        val noteDao = NoteRoomDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
        allNotes = repository.allNote
        allDeletedNotes = repository.allDeletedNotes

    }

    /**
     * Calls the insert() method from de repository class.
     *
     * @param note The note object that will be insert into the database.
     */
    fun insert(note: Note) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insert(note)
        }
    }

    /**
     * Calls the update() method from de repository class.
     *
     * @param note The note object that will be updated in the database.
     */
    fun update(note: Note) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.update(note)
        }
    }

    /**
     * Calls the delete method from de repository class.
     *
     * @param note The note object that will be deleted from the database.
     */
    fun delete(note: Note) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.delete(note)
        }
    }

    /**
     * Calls the deleteAllNotes() method from de repository class.
     */
    fun deleteAllNotes() {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deleteAllNotes()
        }
    }

    /**
     * Calls the deleteAllDeletedNotes() method from de repository class.
     */
    fun deleteAllDeletedNotes() {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deleteAllDeletedNotes()
        }
    }

    /**
     * Calls the sortNotesASC() method from de repository class.
     */
    fun sortAllNotesASC() {
        viewModelScope.launch(Dispatchers.Default) {
            repository.sortNotesASC()
        }
    }
}