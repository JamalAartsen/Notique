package com.example.quicknote.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.quicknote.Note

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note)

    @Delete
    fun delete(note: Note)

    @Update
    fun update(note: Note)

    @Query("DELETE FROM note")
    fun deleteAllNotes();

    @Query("SELECT * FROM note")
    fun getAllNotes(): LiveData<List<Note>>
}