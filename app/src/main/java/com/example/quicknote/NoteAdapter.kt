package com.example.quicknote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.notes_row.view.*
import java.util.*
import kotlin.collections.ArrayList

class NoteAdapter(var notes: MutableList<Note>, val context: Context, var onLongPresDelete: OnClickListener, var addDeleteNote: InsertDeleteNote) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private val notesListSearch: MutableList<Note>
    private val arraylist: ArrayList<Note>
    private var deletedPosition: Int = 0
    private var deletedNote: Note = Note(0, "", "", "")

    init {
        notesListSearch = ArrayList(notes)
        arraylist = ArrayList<Note>()
        arraylist.addAll(notes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.notes_row, parent, false))
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note: Note = notes[position]

        holder.noteTitle?.text = note.titleNote
        holder.noteDescription?.text = note.descriptionNote
        holder.noteDate?.text = note.dateNote

        holder.cardView?.setOnLongClickListener {
            deletedPosition = holder.adapterPosition
            deletedNote = notes[holder.adapterPosition]

            onLongPresDelete.onLongPressDelete(holder.adapterPosition)
            Snackbar.make(it, R.string.Note_deleted, Snackbar.LENGTH_LONG).apply {
                setAction("Undo") {
                    notes.add(deletedPosition, deletedNote)
                    notifyItemInserted(deletedPosition)
                    addDeleteNote.insertDeletedNote(deletedPosition, deletedNote)
                    dismiss()
                }
                show()
            }
            false
        }
    }

    fun swapList(newList: MutableList<Note>) {
        notes = newList
        notifyDataSetChanged()
    }

    fun filter(charText: String) {
        var charText1 = charText
        charText1 = charText1.toLowerCase(Locale.getDefault())
        notes.clear()
        if (charText1.isEmpty()) {
            notes.addAll(arraylist)
        } else {
            for (note in arraylist) {
                if (note.titleNote?.toLowerCase(Locale.getDefault())!!.contains(charText1)) {
                    notes.add(note)
                }
            }
        }
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val noteTitle: TextView? = view.titleNote
        val noteDescription: TextView? = view.descriptionNote
        val noteDate: TextView? = view.dateNote
        val cardView: LinearLayout? = view.cardView
    }
}

interface OnClickListener {
    fun onLongPressDelete(position: Int)
}

interface InsertDeleteNote {
    fun insertDeletedNote(position: Int, note: Note)
}

