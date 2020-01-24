package com.example.quicknote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.android.synthetic.main.content_add_note.*


class AddNote : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val note = Note(0, title_add_note.text.toString(), description_add_note.text.toString())

            val intentSendData = Intent(this, MainActivity::class.java).apply {
                putExtra(SEND_NOTE_DATA, note)
            }

            setResult(Activity.RESULT_OK, intentSendData)
            finish()
        }
    }
}
