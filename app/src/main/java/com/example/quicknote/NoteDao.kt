package com.example.quicknote

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.quicknote.Note

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

    @Query("SELECT * FROM note_table")
    fun getAllNotes(): LiveData<MutableList<Note>>
}