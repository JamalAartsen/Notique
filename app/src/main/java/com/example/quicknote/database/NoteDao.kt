package com.example.quicknote.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.quicknote.model.Note
import com.example.quicknote.model.NoteDeleted

@Dao
interface NoteDao {

    @Insert
    fun insert(note: Note)

    @Delete
    fun delete(note: Note)

    @Update
    fun update(note: Note)

    @Query("DELETE FROM note_table")
    fun deleteAllNotes()

    @Query("DELETE FROM note_table WHERE deleted=1")
    fun deleteAllDeletedNotes()

    @Query("SELECT * FROM note_table WHERE deleted=0")
    fun getAllNotes(): LiveData<MutableList<Note>>

    @Query("SELECT * FROM note_table WHERE deleted=1")
    fun getAllDeletedNotes(): LiveData<MutableList<Note>>

    @Query("SELECT * FROM note_table ORDER BY title_note ASC")
    fun orderNotesASC(): LiveData<MutableList<Note>>
}