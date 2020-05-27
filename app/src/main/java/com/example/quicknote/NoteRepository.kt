package com.example.quicknote

import androidx.lifecycle.LiveData
import com.example.quicknote.database.NoteDao
import com.example.quicknote.model.Note
import com.example.quicknote.model.NoteDeleted

/**
 * Isolates data. Provides clean api. Allows you to use multiple backends.
 */
class NoteRepository(private val noteDao: NoteDao) {

    //Variables
    val allNote: LiveData<MutableList<Note>> = noteDao.getAllNotes()
    val allDeletedNotes: LiveData<MutableList<Note>> = noteDao.getAllDeletedNotes()

    /**
     * Calls the insert() method from the noteDao interface.
     *
     * @param note The note object that will be insert into the database.
     */
    fun insert(note: Note) {
        noteDao.insert(note)
    }

    /**
     * Calls the update() method from the noteDao interface.
     *
     * @param note The note object that will be update in the database.
     */
    fun update(note: Note) {
        noteDao.update(note)
    }

    /**
     * Calls the delete() method from the noteDao interface.
     *
     * @param note The note object that will be delete from the database.
     */
    fun delete(note: Note) {
        noteDao.delete(note)
    }

    /**
     * Calls the deleteAllNotes() method from the noteDao interface.
     */
    fun deleteAllNotes() {
        noteDao.deleteAllNotes()
        allNote
    }

    /**
     * Calls the deleteAllDeletedNotes() method from the noteDao interface.
     */
    fun deleteAllDeletedNotes() {
        noteDao.deleteAllDeletedNotes()
    }

    /**
     * Calls the orderNotesASC() method from the noteDao interface.
     */
    fun sortNotesASC() {
        noteDao.orderNotesASC()
    }
}