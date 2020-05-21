package com.example.quicknote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quicknote.model.Note
import com.example.quicknote.model.NoteDeleted
import kotlinx.android.synthetic.main.notes_row_deleted.view.*

class NoteDeletedAdapter(var notesDeletedList: MutableList<Note>, val context: Context) :
    RecyclerView.Adapter<NoteDeletedAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.notes_row_deleted, parent, false))
    }

    override fun getItemCount(): Int {
        return notesDeletedList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val noteDeleted: Note = notesDeletedList[position]

        holder.noteTitleDeleted?.text = noteDeleted.titleNote
        holder.noteDescriptionDeleted?.text = noteDeleted.descriptionNote
        holder.noteDateDeleted?.text = noteDeleted.dateNote
    }

    fun updateDeletedList(newList: MutableList<Note>) {
        notesDeletedList = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val noteTitleDeleted: TextView? = view.titleNoteDeleted
        val noteDescriptionDeleted: TextView? = view.descriptionNoteDeleted
        val noteDateDeleted: TextView? = view.dateNoteDeleted
        val cardViewDeleted: LinearLayout? = view.cardViewDeleted

    }
}