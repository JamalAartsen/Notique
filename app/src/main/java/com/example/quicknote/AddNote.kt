package com.example.quicknote

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.android.synthetic.main.content_add_note.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class AddNote : AppCompatActivity() {

    var db = NoteRoomDatabase
    private val executor: Executor =
        Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            insertNote(Note(0, title_add_note.text.toString(), description_add_note.text.toString()))
        }
    }

    fun insertNote(note: Note) {
        executor.execute {
            db.getDatabase(this).noteDao().insert(note)
        }
    }

    fun refreshActivity() {
        val i = Intent(this, MainActivity::class.java)
        overridePendingTransition(0, 0)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        finish()
        overridePendingTransition(0, 0)
        startActivity(i)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        refreshActivity()
    }

}
