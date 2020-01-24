package com.example.quicknote

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class NoteViewModel(application: Application): AndroidViewModel(application) {

    private val repository: NoteRepository
    val allNotes: LiveData<List<Note>>

    init {
        val noteDao = NoteRoomDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
        allNotes = repository.allNote
    }

    fun insert(note: Note) {
        repository.insert(note)
    }
}