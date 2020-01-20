package com.example.quicknote

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private var notes: List<Note> = ArrayList()
    var db = NoteRoomDatabase
    private val executor: Executor =
        Executors.newSingleThreadExecutor()
    private var noteAdapter: NoteAdapter? = null
    var count = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        db.getDatabase(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = NoteAdapter(notes, this)

        fab.setOnClickListener { view ->
            count++
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            insertNotes(Note(count, "Hello", "Ik ben Jamal"))
            Log.d("Count", "$count")
        }

        getAllNotes()
    }

    fun getAllNotes() {
        executor.execute {
            notes = db.getDatabase(this).noteDao().getAllNotes()
            runOnUiThread {
                updateUI()
            }
        }
    }

    fun insertNotes(note: Note) {
        executor.execute {
            db.getDatabase(this).noteDao().insert(note)
            getAllNotes()
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
}
