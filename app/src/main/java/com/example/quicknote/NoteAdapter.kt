package com.example.quicknote

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.quicknote.model.Note
import kotlinx.android.synthetic.main.notes_row.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class NoteAdapter(var notes: MutableList<Note>, val context: Context, var onClick: OnClickListener, var addDeleteNote: InsertDeleteNote) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>(), Filterable {

    private var notesListSearch: MutableList<Note>
    private var deletedPosition: Int = 0
    private var deletedNote: Note =
        Note(0, "", "", "", null, null)
    private var selectedItem = false
    private var hashMapSelected = HashMap<Int, Boolean>()
    private var hashMapAllPositions = HashMap<Int, Int>()
    private val undoPayload = 11

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

        holder.cardView?.setOnLongClickListener {
            deletedPosition = holder.adapterPosition
            deletedNote = notes[holder.adapterPosition]

            hashMapAllPositions[position] = position

            selectedItem = true
            hashMapSelected[position] = selectedItem
            onClick.onLongPressDelete(holder.adapterPosition, deletedPosition, deletedNote)
            true
        }


        holder.cardView?.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                onClick.onClick(holder.adapterPosition)
            }
            Log.d("ClickPosition", "${holder.adapterPosition}")
        }
        Log.d("Payload", "dw2f3wf")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        onBindViewHolder(holder, position)
    }


    fun swapList(newList: MutableList<Note>) {
        val result = DiffUtil.calculateDiff(NoteListDiffUtilCallback(this.notes, newList))
        notes = newList
        notesListSearch = ArrayList(notes)
        result.dispatchUpdatesTo(this)
//        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        notifyItemRemoved(position)
    }

    fun updateSortedList(newList: MutableList<Note>) {
        notes = newList
        notesListSearch = ArrayList(notes)
        notifyDataSetChanged()
    }

    fun insertDeletedNote(deletedPosition: Int, deletedNote: Note) {
         addDeleteNote.insertDeletedNote(deletedPosition, deletedNote)
    }

    fun insertHardcoded(newList: MutableList<Note>) {
        notes = newList
        notifyItemInserted(0)
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
        val contaier: CardView? = view.cardViewlayout
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
    fun onLongPressDelete(position: Int, deletedPosition: Int, deletedNote: Note)
    fun onClick(position: Int)
}

interface InsertDeleteNote {
    fun insertDeletedNote(position: Int, note: Note)
}

class NoteListDiffUtilCallback(private var oldItems: MutableList<Note>, private var newItems: MutableList<Note>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].id == newItems[newItemPosition].id
    }

    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val (_, value, name) = oldItems[oldItemPosition]
        val (_, value1, name1) = newItems[newItemPosition]

        return name == name1 && value == value1
    }
}
