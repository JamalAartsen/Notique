package com.example.quicknote

import androidx.lifecycle.LiveData
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class NoteRepository(private val noteDao: NoteDao) {

    private val mExecutor: Executor =
        Executors.newSingleThreadExecutor()

    val allNote: LiveData<List<Note>> = noteDao.getAllNotes()

     fun insert(note: Note) {
         mExecutor.execute {
             noteDao.insert(note)
         }
    }
}