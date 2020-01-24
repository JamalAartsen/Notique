package com.example.quicknote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), RecyclerView.OnItemTouchListener {

    private var notes: List<Note> = ArrayList()
    var db = NoteRoomDatabase
    private var noteAdapter: NoteAdapter? = null
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var gestureDetector: GestureDetector
    private var modifyPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        db.getDatabase(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = NoteAdapter(notes, this)

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        noteViewModel.allNotes.observe(this, Observer<List<Note>> { notes ->
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

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_search -> true
            else -> super.onOptionsItemSelected(item)
        }
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
}
