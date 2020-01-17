package com.example.quicknote

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(@PrimaryKey val id: Int,
                @ColumnInfo(name = "title_note") val titleNote: String?,
                @ColumnInfo(name = "description_note") val descriptionNote: String?) {
}