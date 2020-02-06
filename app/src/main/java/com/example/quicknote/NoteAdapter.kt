package com.example.quicknote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.notes_row.view.*

class NoteAdapter(var notes: MutableList<Note>, val context: Context, var onDeleteClickListener: OnDeleteClickListener) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private val notesListSearch: MutableList<Note>

    init {
        notesListSearch = ArrayList(notes)
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
        holder.noteDeleteImage?.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                onDeleteClickListener.onDeleteClick(holder.adapterPosition)
            }
        })
    }

    fun swapList(newList: MutableList<Note>) {
        notes = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val noteTitle: TextView? = view.titleNote
        val noteDescription: TextView? = view.descriptionNote
        val noteDate: TextView? = view.dateNote
        val noteDeleteImage: ImageView? = view.delete_single_item
        val cardView: CardView? = view.cardViewlayout
    }

}

interface OnDeleteClickListener {
    fun onDeleteClick(position: Int)
}

