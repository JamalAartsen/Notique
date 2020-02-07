package com.example.quicknote

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), RecyclerView.OnItemTouchListener {

    private var notes: MutableList<Note> = ArrayList()
    private var noteAdapter: NoteAdapter? = null
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var gestureDetector: GestureDetector
    private lateinit var items: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        toolbar.setNavigationIcon(R.drawable.ic_clip)

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = NoteAdapter(notes, applicationContext, object : OnClickListener {
                override fun onLongPressDelete(position: Int) {

                }
            }, object : InsertDeleteNote {
                override fun insertDeletedNote(position: Int, note: Note) {

                }
            })
        }

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        noteViewModel.allNotes.observe(this, Observer<MutableList<Note>> { notes ->
            this.notes = notes
            updateUI()
            if (notes.isEmpty()) {
                recyclerView.visibility = View.GONE
                textView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                textView.visibility = View.GONE
            }
        })

        fab.setOnClickListener {
            val intentAddNote = Intent(this, AddNote::class.java)
            startActivityForResult(intentAddNote, ADD_REQUEST_CODE)
        }

        gestureDetector = GestureDetector(this, object : SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }
        })

        recyclerView.addOnItemTouchListener(this)
    }

    private fun updateUI() {
        if (noteAdapter == null) {
            noteAdapter = NoteAdapter(notes, applicationContext, object : OnClickListener {
                override fun onLongPressDelete(position: Int) {
                    noteViewModel.delete(notes[position])
                    notes.removeAt(position)
                    noteAdapter?.notifyItemRemoved(position)

                }
            }, object : InsertDeleteNote {
                override fun insertDeletedNote(position: Int, note: Note) {
                    noteViewModel.insert(note)
                }
            })
            recyclerView.adapter = noteAdapter
        } else {
            noteAdapter?.swapList(notes)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.action_share).apply {
            isVisible = false // Hide item for this activity
        }

        val searchItem = menu.findItem(R.id.action_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { noteAdapter?.filter(it) }
                    return false
                }
            })
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_delete_notes -> popUpDialogDeleteAllNotes()
            R.id.action_filter_list -> showFilterDialog()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val addNote: Note? = data?.getParcelableExtra(SEND_NOTE_DATA)
                addNote?.let { noteViewModel.insert(it) }
            }
        }

        if (requestCode == EDIT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val editNote: Note? = data?.getParcelableExtra(SEND_EDITED_NOTE)
                editNote?.titleNote
                editNote?.let { noteViewModel.update(it) }
            }
        }
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val child = recyclerView.findChildViewUnder(e.x, e.y)
        val noteAdapterPosition = child?.let { recyclerView.getChildAdapterPosition(it) }

        if (child != null && gestureDetector.onTouchEvent(e)) {
            val intentEditNote = Intent(this, EditNote::class.java).apply {
                putExtra(SEND_DATA_EDIT_NOTE, noteAdapterPosition?.let { notes[it] })
            }
            startActivityForResult(intentEditNote, EDIT_REQUEST_CODE)
        }

        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }

    private fun sortNotes(item: String) {
        if (item == getString(R.string.ascending)) {
            notes.sortWith(Comparator { o1, o2 -> o1.titleNote.toString().compareTo(o2.titleNote.toString()) })
            updateUI()
        } else if (item == getString(R.string.descending)) {
            notes.sortWith(Comparator { o1, o2 -> o2.titleNote.toString().compareTo(o1.titleNote.toString()) })
            updateUI()
        } else if (item == getString(R.string.date_newest)) {
            notes.sortWith(Comparator { o1, o2 -> o1.dateNote.toString().compareTo(o2.dateNote.toString()) })
            updateUI()
        } else {
            notes.sortWith(Comparator { o1, o2 -> o2.dateNote.toString().compareTo(o1.dateNote.toString()) })
            updateUI()
        }
    }

    private fun popUpDialogDeleteAllNotes() {
        val builder = AlertDialog.Builder(this).apply {
            setMessage(R.string.delete_all_notes_message)
            setCancelable(true)
            setPositiveButton(R.string.yes) { dialog, id ->
                noteViewModel.deleteAllNotes()
                dialog.cancel()
                Toast.makeText(applicationContext, R.string.delete_all_notes_message, Toast.LENGTH_SHORT).show()
            }
            setNegativeButton(R.string.no) { dialog, id -> dialog.cancel() }
        }

        builder.create().show()
    }

    private fun showFilterDialog() {
        val alertDialog = AlertDialog.Builder(this).apply {
            setTitle(R.string.sort_by)
            setIcon(R.drawable.ic_filter_list)
            setSingleChoiceItems(R.array.filter_notes, -1) { dialog, which ->
                items = arrayListOf(getString(R.string.ascending), getString(R.string.descending),
                    getString(R.string.date_newest), getString(R.string.date_oldest))
                when (which) {
                    0 -> { sortNotes(items[which]) }
                    1 -> { sortNotes(items[which]) }
                    2 -> { sortNotes(items[which]) }
                    3 -> { sortNotes(items[which]) }
                }
            }
            setPositiveButton(R.string.sort) { dialog, _ -> dialog.cancel() }
            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
        }
        alertDialog.create().show()
    }
}
