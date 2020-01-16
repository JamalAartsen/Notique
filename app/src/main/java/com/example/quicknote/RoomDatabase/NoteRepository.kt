package com.example.quicknote.RoomDatabase

import androidx.lifecycle.LiveData
import com.example.quicknote.Note


class NoteRepository(private val noteDao: NoteDao) {

    private val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()


    fun insert(note: Note){
        noteDao.insert(note)
    }
}