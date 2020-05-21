package com.example.quicknote.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(@PrimaryKey(autoGenerate = true) val id: Int,
                @ColumnInfo(name = "title_note") var titleNote: String,
                @ColumnInfo(name = "description_note") var descriptionNote: String?,
                @ColumnInfo(name = "date-note") var dateNote: String?,
                @ColumnInfo(name = "image_note") var imageUriNote: ByteArray?,
                @ColumnInfo(name = "deleted") var isDeleted: Boolean?): Parcelable {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createByteArray(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(titleNote)
        parcel.writeString(descriptionNote)
        parcel.writeString(dateNote)
        parcel.writeByteArray(imageUriNote)
        parcel.writeByte((if (isDeleted!!) 1 else 0).toByte())
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + titleNote.hashCode()
        result = 31 * result + (descriptionNote?.hashCode() ?: 0)
        result = 31 * result + (dateNote?.hashCode() ?: 0)
        result = 31 * result + (imageUriNote?.contentHashCode() ?: 0)
        result = 31 * result + (isDeleted?.hashCode() ?: 0)
        return result
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