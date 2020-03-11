package com.example.quicknote

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.quicknote.common.Common
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.notes_row.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class NoteAdapter(var notes: MutableList<Note>, val context: Context, var onClick: OnClickListener, var addDeleteNote: InsertDeleteNote) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>(), Filterable {

    private var notesListSearch: MutableList<Note>
    private var deletedPosition: Int = 0
    private var deletedNote: Note = Note(0, "", "", "", null)
    private var selectedItem = false
    private var hashMapSelected = HashMap<Int, Boolean>()
    private var hashMapAllPositions = HashMap<Int, Int>()
    var row_index = -1

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
//        val note: Note = notes[position]
//
//        holder.noteTitle?.text = note.titleNote
//        holder.noteDescription?.text = note.descriptionNote
//        holder.noteDate?.text = note.dateNote
//
//        holder.cardView?.setOnLongClickListener {
//            deletedPosition = holder.adapterPosition
//            deletedNote = notes[holder.adapterPosition]
//
//            hashMapAllPositions[position] = position
//
//            selectedItem = true
//            hashMapSelected[position] = selectedItem
//            onClick.onLongPressDelete(holder.adapterPosition)
////            Snackbar.make(it, R.string.Note_deleted, Snackbar.LENGTH_LONG).apply {
////                setAction("Undo") {
////                    if (deletedPosition != RecyclerView.NO_POSITION) {
////                        notes.add(deletedPosition, deletedNote)
////                        notifyItemInserted(deletedPosition)
////                        addDeleteNote.insertDeletedNote(deletedPosition, deletedNote)
////                        dismiss()
////                    }
////                }
////                show()
////            }
//
//            if (selectedItem) {
//                holder.cardView.setBackgroundResource(R.color.grey_light)
//            } else {
//                holder.cardView.setBackgroundResource(R.color.white)
//            }
//            true
//        }
//
//        holder.cardView?.setOnClickListener {
//            if (hashMapSelected[position] == true) {
//                holder.cardView.setBackgroundResource(R.color.white)
//                selectedItem = false
//                hashMapSelected.remove(position)
//                hashMapAllPositions.remove(position)
//                // todo een hashmap maken
//            } else {
//                val positionAdapter = holder.adapterPosition
//
//                if (hashMapSelected[position] == false || hashMapSelected.isNotEmpty()) {
//                    holder.cardView.setBackgroundResource(R.color.grey_light)
//                    hashMapSelected[position] = true
//                    hashMapAllPositions[position] = position
//                } else {
//                    if (positionAdapter != RecyclerView.NO_POSITION) {
//                        onClick.onClick(holder.adapterPosition)
//                    }
//                }
//            }
//            Log.d("ClickPosition", "${holder.adapterPosition}")
//        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val note: Note = notes[position]

        holder.noteTitle?.text = note.titleNote
        holder.noteDescription?.text = note.descriptionNote
        holder.noteDate?.text = note.dateNote

        holder.cardView?.setOnLongClickListener {
            deletedPosition = holder.adapterPosition
            deletedNote = notes[holder.adapterPosition]

            hashMapAllPositions[position] = position

            selectedItem = true
            hashMapSelected[position] = selectedItem
            onClick.onLongPressDelete(holder.adapterPosition)
            Snackbar.make(it, R.string.Note_deleted, Snackbar.LENGTH_LONG).apply {
                setAction("Undo") {
                    if (deletedPosition != RecyclerView.NO_POSITION) {
                        notes.add(deletedPosition, deletedNote)
                        notifyItemInserted(deletedPosition)
                        addDeleteNote.insertDeletedNote(deletedPosition, deletedNote)
                        dismiss()
                    }
                }
                show()
            }

//            row_index = position
//            Common.currentNote = notes[position]
//
//            if (row_index == position) {
//                holder.cardView?.setBackgroundResource(R.color.grey_light)
//            } else {
//                holder.cardView?.setBackgroundResource(R.color.white)
//            }
            true
        }


        holder.cardView?.setOnClickListener {
//            if (hashMapSelected[position] == true) {
//                holder.cardView.setBackgroundResource(R.color.white)
//                selectedItem = false
//                hashMapSelected.remove(position)
//                hashMapAllPositions.remove(position)
//                // todo een hashmap maken
//            } else {
//                val positionAdapter = holder.adapterPosition
//
//                if (hashMapSelected[position] == false || hashMapSelected.isNotEmpty()) {
//                    holder.cardView.setBackgroundResource(R.color.grey_light)
//                    hashMapSelected[position] = true
//                    hashMapAllPositions[position] = position
//                } else {
//                    if (positionAdapter != RecyclerView.NO_POSITION) {
//                        onClick.onClick(holder.adapterPosition)
//                    }
//                }
//            }

            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                onClick.onClick(holder.adapterPosition)
            }
            Log.d("ClickPosition", "${holder.adapterPosition}")
        }

//        if (payloads.contains("JAMAL")) {
//            holder.cardView?.setBackgroundResource(R.color.white)
//            hashMapSelected.remove(position)
//        }
    }

    fun swapList(newList: MutableList<Note>) {
        notes = newList
        notesListSearch = ArrayList(notes)
        notifyDataSetChanged()
    }

    fun clearAllHighLightedNotes() {
        notifyItemRangeChanged(0, hashMapAllPositions.size, "JAMAL")
        Log.d("ClearNote", "All notes are clear highlight.")
    }

    fun allSelectedNotes(): String {
        return "${hashMapSelected.size}"
    }

    fun filter(charText: String) {
        var charText1 = charText
        charText1 = charText1.toLowerCase(Locale.getDefault())
        notes.clear()
        if (charText1.isEmpty()) {
            notes.addAll(notesListSearch)
        } else {
            for (note in notesListSearch) {
                if (note.titleNote.toLowerCase(Locale.getDefault()).contains(charText1)) {
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
        val bijlageImageNote: ImageView? = view.note_logo
    }

    fun noteListSearch() {
        notesListSearch = ArrayList(notes)
    }

    override fun getFilter(): Filter {
        return searchFilterList
    }

    private val searchFilterList = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: MutableList<Note> = ArrayList()

            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(notesListSearch)
            } else {
                val filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim()

                for (note in notesListSearch) {
                    if (note.titleNote.toLowerCase(Locale.getDefault()).contains(filterPattern)) {
                        filteredList.add(note)
                    }
                }
            }

            return FilterResults().apply {
                values = filteredList
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notes.clear()
            notes.addAll(results!!.values as MutableList<Note>)
            notifyDataSetChanged()
        }
    }
}

interface OnClickListener {
    fun onLongPressDelete(position: Int)
    fun onClick(position: Int)
}

interface InsertDeleteNote {
    fun insertDeletedNote(position: Int, note: Note)
}

