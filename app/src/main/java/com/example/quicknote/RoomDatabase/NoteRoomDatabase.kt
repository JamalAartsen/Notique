package com.example.quicknote.RoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.quicknote.Note

@Database(entities = arrayOf(Note::class), version = 1, exportSchema = false)
abstract class NoteRoomDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        private var INSTANCE: NoteRoomDatabase? = null

        fun getDatabase(context: Context): NoteRoomDatabase {
            if (INSTANCE == null) {

                INSTANCE = Room.databaseBuilder(
                    context,
                    NoteRoomDatabase::class.java, "note_database"
                ).build()

            }
            return INSTANCE as NoteRoomDatabase
        }
    }
}
