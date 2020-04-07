package com.example.quicknote

import androidx.lifecycle.LiveData
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class NoteRepository(private val noteDao: NoteDao) {

    private val mExecutor: Executor =
        Executors.newSingleThreadExecutor() as Executor

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
}