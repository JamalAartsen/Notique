package com.example.quicknote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit_note.*
import kotlinx.android.synthetic.main.content_edit_note.*

class EditNote : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)
        setSupportActionBar(toolbar)

        val note = intent.getParcelableExtra<Note?>(SEND_DATA_EDIT_NOTE)
        if (note != null) {
            title_edit_note.setText(note.titleNote)
            description_edit_note.setText(note.descriptionNote)
        }

        fab.setOnClickListener {

            note?.apply {
                titleNote = title_edit_note.text.toString()
                descriptionNote = description_edit_note.text.toString()
            }

            val editIntent = Intent(this, MainActivity::class.java).apply {
                putExtra(SEND_EDITED_NOTE, note)
            }

            setResult(Activity.RESULT_OK, editIntent)
            finish()
        }
    }

    fun shareData() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            putExtra(Intent.EXTRA_SUBJECT, title_edit_note.text.toString())
            putExtra(Intent.EXTRA_TEXT, description_edit_note.text.toString())
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.action_search).apply {
            isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.itemId == R.id.action_share) {
            shareData()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
