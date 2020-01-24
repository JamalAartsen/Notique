package com.example.quicknote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_add_note.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    companion object {
        val REQUESTCODE = 1
    }

    private var notes: List<Note> = ArrayList()
    var db = NoteRoomDatabase
    private var noteAdapter: NoteAdapter? = null
    private lateinit var noteViewModel: NoteViewModel

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
            startActivityForResult(intentAddNote, REQUESTCODE)
        }
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
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUESTCODE) {
            if (resultCode == Activity.RESULT_OK) {
                val note: Note? = data?.getParcelableExtra(SEND_NOTE_DATA)

                note?.let { noteViewModel.insert(it) }
            }
        }
    }
}
