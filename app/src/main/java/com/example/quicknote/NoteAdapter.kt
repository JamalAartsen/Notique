package com.example.quicknote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.notes_row.view.*

class NoteAdapter(var notes: List<Note>, val context: Context) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

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
    }

    fun swapList(newList: List<Note>) {
        notes = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val noteTitle: TextView? = view.titleNote
        val noteDescription: TextView? = view.descriptionNote
    }
}