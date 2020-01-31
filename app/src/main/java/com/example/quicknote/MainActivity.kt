package com.example.quicknote

import android.app.Activity
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), RecyclerView.OnItemTouchListener {

    private var notes: MutableList<Note> = ArrayList()
    var db = NoteRoomDatabase
    private var noteAdapter: NoteAdapter? = null
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var gestureDetector: GestureDetector
    private var modifyPosition = 0
    private lateinit var items: ArrayList<String>
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        db.getDatabase(this)
        sharedPreferences = getSharedPreferences("jamal", Context.MODE_PRIVATE)

        // todo
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        Log.d("Date", "$currentDate")

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = NoteAdapter(notes, this)

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        noteViewModel.allNotes.observe(this, Observer<MutableList<Note>> { notes ->
            this.notes = notes
            updateUI()
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

        sortButton.setOnClickListener {
            showFilterDialog()
        }

        recyclerView.addOnItemTouchListener(this)
    }

    private fun updateUI() {
        if (noteAdapter == null) {
            noteAdapter = NoteAdapter(notes, applicationContext)
            recyclerView.adapter = noteAdapter
        } else {
            noteAdapter?.swapList(notes)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.action_share).apply {
            isVisible = false // Hide item for this activity
        }

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.itemId == R.id.action_delete_notes) {
            popUpDialogDeleteAllNotes()
            return true
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val child = recyclerView.findChildViewUnder(e.x, e.y)
        val noteAdapterPosition = child?.let { recyclerView.getChildAdapterPosition(it) }

        if (child != null && gestureDetector.onTouchEvent(e)) {
            val intentEditNote = Intent(this, EditNote::class.java).apply {
                if (noteAdapterPosition != null) {
                    modifyPosition = noteAdapterPosition
                }
                putExtra(SEND_DATA_EDIT_NOTE, noteAdapterPosition?.let { notes[it] })
            }
            startActivityForResult(intentEditNote, EDIT_REQUEST_CODE)
        }

        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun sortNotes(item: String) {
        if (item == getString(R.string.ascending)) {
            notes.sortWith(Comparator { o1, o2 ->
                o1.titleNote.toString().compareTo(o2.titleNote.toString())
            })

            updateUI()
        } else if (item == getString(R.string.descending)) {
            notes.sortWith(Comparator { o1, o2 ->
                o2.titleNote.toString().compareTo(o1.titleNote.toString())
            })
            updateUI()
        } else if (item == getString(R.string.date_newest)) {
            notes.sortWith(Comparator { o1, o2 ->  o1.dateNote.toString().compareTo(o2.dateNote.toString())
            })

            updateUI()
        } else {
            notes.sortWith(Comparator { o1, o2 ->  o2.dateNote.toString().compareTo(o1.dateNote.toString())
            })

            updateUI()
        }

    }

    fun popUpDialogDeleteAllNotes() {
        val builder = AlertDialog.Builder(this).apply {
            setMessage("Are you sure you want to delete all your notes?")
            setCancelable(true)
            setPositiveButton(
                "Yes"
            ) { dialog, id ->
                noteViewModel.deleteAllNotes()
                dialog.cancel()
            }
            setNegativeButton(
                "No"
            ) { dialog, id -> dialog.cancel() }
        }

        builder.create().apply {
            show()
        }
    }

    fun showFilterDialog() {
        val alertDialog = AlertDialog.Builder(this).apply {
            setTitle("Sort By")
            setIcon(R.drawable.ic_filter_list)
            setSingleChoiceItems(
                R.array.filter_notes,
                -1
            ) { dialog, which ->
                items = arrayListOf(getString(R.string.ascending), getString(R.string.descending), getString(R.string.date_newest), getString(R.string.date_oldest))
                when (which) {
                    0 -> { sortNotes(items[which]) }
                    1 -> { sortNotes(items[which]) }
                    2 -> { sortNotes(items[which]) }
                    3 -> { sortNotes(items[which]) }
                }
            }

            setPositiveButton(
                "OK"
            ) { dialog, _ -> dialog.cancel() }
        }

        alertDialog.create().apply {
            show()
        }
    }
}
