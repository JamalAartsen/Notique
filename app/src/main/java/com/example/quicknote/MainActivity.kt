package com.example.quicknote

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), RecyclerView.OnItemTouchListener {

    private var notes: List<Note> = ArrayList()
    var db = NoteRoomDatabase
    private val executor: Executor =
        Executors.newSingleThreadExecutor()
    private var noteAdapter: NoteAdapter? = null
    private var mGestureDetector: GestureDetector? = null
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
            insertNote(Note(count, "Hello", "Ik ben Jamal"))
            Log.d("Count", "$count")
        }

        //Delete item with long click on the item
        mGestureDetector = GestureDetector(this, object : SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)
                val child = recyclerView.findChildViewUnder(e.x, e.y)
                if (child != null) {
                    val adapterPosition = recyclerView.getChildAdapterPosition(child)
                    deleteNote(notes[adapterPosition])
                }
            }
        })

        recyclerView.addOnItemTouchListener(this)
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

    fun insertNote(note: Note) {
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

    fun deleteNote(note: Note) {
        executor.execute {
            db.getDatabase(this).noteDao().delete(note)
            getAllNotes()
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

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        mGestureDetector?.onTouchEvent(e)
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
