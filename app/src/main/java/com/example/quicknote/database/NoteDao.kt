package com.example.quicknote.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.quicknote.model.Note
import com.example.quicknote.model.NoteDeleted

/**
 * This interface defines the standard operations to be performed on a model object(s).
 */
@Dao
interface NoteDao {

    /**
     * Method that insert a note object into the database.
     *
     * @param note The note object that will be insert into the database.
     */
    @Insert
    fun insert(note: Note)

    /**
     * Method that delte a note object from the database.
     *
     * @param note The note object that will be delete from the database.
     */
    @Delete
    fun delete(note: Note)

    /**
     * Method that update a note object in the database.
     *
     * @param note The note object that will be update in the database.
     */
    @Update
    fun update(note: Note)

    /**
     * Method that delete all the note objects from the database.
     */
    @Query("DELETE FROM note_table")
    fun deleteAllNotes()

    /**
     * Method that delete all the "deleted" notes from the database.
     */
    @Query("DELETE FROM note_table WHERE deleted=1")
    fun deleteAllDeletedNotes()

    /**
     * Method that get all the note objects from the database.
     */
    @Query("SELECT * FROM note_table WHERE deleted=0")
    fun getAllNotes(): LiveData<MutableList<Note>>

    /**
     * Method that delete all the "deleted" note objects from the database.
     */
    @Query("SELECT * FROM note_table WHERE deleted=1")
    fun getAllDeletedNotes(): LiveData<MutableList<Note>>

    /**
     * Method that order all the note objects from the database from A-Z.
     */
    @Query("SELECT * FROM note_table ORDER BY title_note ASC")
    fun orderNotesASC(): LiveData<MutableList<Note>>
}