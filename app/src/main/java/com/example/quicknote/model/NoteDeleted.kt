package com.example.quicknote.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deleted_note_table")
data class NoteDeleted(@PrimaryKey(autoGenerate = true) val id: Int,
                       @ColumnInfo(name = "title_note") var titleNoteDeleted: String,
                       @ColumnInfo(name = "description_note") var descriptionNoteDeleted: String?,
                       @ColumnInfo(name = "date-note") var dateNoteDeleted: String?,
                       @ColumnInfo(name = "image_note") var imageUriNoteDeleted: ByteArray?): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createByteArray()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(titleNoteDeleted)
        parcel.writeString(descriptionNoteDeleted)
        parcel.writeString(dateNoteDeleted)
        parcel.writeByteArray(imageUriNoteDeleted)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }
}