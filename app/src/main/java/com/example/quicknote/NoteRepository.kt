package com.example.quicknote

import androidx.lifecycle.LiveData
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class NoteRepository(private val noteDao: NoteDao) {

    private val mExecutor: Executor =
        Executors.newSingleThreadExecutor()

    val allNote: LiveData<MutableList<Note>> = noteDao.getAllNotes()

    fun insert(note: Note) {
        mExecutor.execute {
            noteDao.insert(note)
        }
    }

    fun update(note: Note) {
        mExecutor.execute {
            noteDao.update(note)
        }
    }

    fun delete(note: Note) {
        mExecutor.execute {
            noteDao.delete(note)
        }
    }

    fun deleteAllNotes() {
        mExecutor.execute {
            noteDao.deleteAllNotes()
            allNote
        }
    }
}