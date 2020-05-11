package com.example.quicknote

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: NoteRepository
    var allNotes: LiveData<MutableList<Note>>

    init {
        val noteDao = NoteRoomDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
        allNotes = repository.allNote

    }

    fun insert(note: Note) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insert(note)
        }
    }

    fun update(note: Note) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.update(note)
        }
    }

    fun delete(note: Note) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.delete(note)
        }
    }

    fun deleteAllNotes() {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deleteAllNotes()
        }
    }

    fun sortAllNotesASC() {
        viewModelScope.launch(Dispatchers.Default) {
            repository.sortNotesASC()
        }
    }
}