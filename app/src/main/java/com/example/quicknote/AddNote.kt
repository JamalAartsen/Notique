package com.example.quicknote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.android.synthetic.main.content_add_note.*
import java.text.SimpleDateFormat
import java.util.*

class AddNote : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        val currentDate: String = SimpleDateFormat(getString(R.string.date_format), Locale.getDefault()).format(Date())

        fab.setOnClickListener {
            val note = Note(0, title_add_note.text.toString(), description_add_note.text.toString(), currentDate)
            val intentSendData = Intent().apply {
                putExtra(SEND_NOTE_DATA, note)
            }

            Toast.makeText(this, R.string.note_added, Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK, intentSendData)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        hideIcon(R.id.action_search, menu)
        hideIcon(R.id.action_delete_notes, menu)
        hideIcon(R.id.action_filter_list, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.itemId == R.id.action_share) {
            shareData(title_add_note.text.toString(), description_add_note.text.toString(), this)
            return true
        }

        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
