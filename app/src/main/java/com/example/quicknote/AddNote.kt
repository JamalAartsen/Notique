package com.example.quicknote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.android.synthetic.main.content_add_note.*

class AddNote : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            val note = Note(0, title_add_note.text.toString(), description_add_note.text.toString())
            val intentSendData = Intent(this, MainActivity::class.java).apply {
                putExtra(SEND_NOTE_DATA, note)
            }

            setResult(Activity.RESULT_OK, intentSendData)
            finish()
        }
    }

    fun shareData() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, title_add_note.text.toString())
            putExtra(Intent.EXTRA_TEXT, description_add_note.text.toString())
            type = "text/plain"
        }

        startActivity(Intent.createChooser(sendIntent, getString(R.string.deel_note)))
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
