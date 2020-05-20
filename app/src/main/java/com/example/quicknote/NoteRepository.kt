package com.example.quicknote

import androidx.lifecycle.LiveData
import com.example.quicknote.Database.NoteDao
import com.example.quicknote.model.Note

// Isolates data. Provides clean api. Allows you to use multiple backends
class NoteRepository(private val noteDao: NoteDao) {
    val allNote: LiveData<MutableList<Note>> = noteDao.getAllNotes()

    fun insert(note: Note) {
        noteDao.insert(note)
    }

    fun update(note: Note) {
        noteDao.update(note)
    }

    fun delete(note: Note) {
        noteDao.delete(note)
    }

    fun deleteAllNotes() {
        noteDao.deleteAllNotes()
        allNote
    }

    fun sortNotesASC() {
        noteDao.orderNotesASC()
    }
}