package com.example.quicknote.activity

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quicknote.*
import com.example.quicknote.model.Note
import com.example.quicknote.model.NoteDeleted

import kotlinx.android.synthetic.main.activity_deleted_notes.*
import kotlinx.android.synthetic.main.content_deleted_notes.*
import java.lang.IndexOutOfBoundsException

class DeletedNotesActivity : AppCompatActivity() {

    private var notesDeletedList: MutableList<Note> = ArrayList()
    private var noteAdapterDeleted: NoteDeletedAdapter? = null
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deleted_notes)
        setSupportActionBar(toolbar)

        setUpAdapterDeletedList()
        recyclerViewDeletedNotes.apply {
            setHasFixedSize(true)
            layoutManager = WrapContentGridLayoutManager(applicationContext, 2)
            adapter = noteAdapterDeleted
            itemAnimator = DefaultItemAnimator()
        }

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        noteViewModel.allDeletedNotes.observe(this, Observer<MutableList<Note>> { notes ->
            notesDeletedList = notes
            noteAdapterDeleted?.updateDeletedList(notes)
        })
    }

    private fun setUpAdapterDeletedList() {
        noteAdapterDeleted = NoteDeletedAdapter(notesDeletedList, applicationContext)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        hideIcon(
            R.id.action_share,
            menu
        )
        hideIcon(R.id.action_bijlage, menu)
        hideIcon(R.id.action_search, menu)
        hideIcon(R.id.action_filter_list, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle clicks on toolbar.
        when(item.itemId) {
            R.id.action_delete_notes -> popUpDialogDeleteAllNotes()
        }

        return super.onOptionsItemSelected(item)
    }

    /**
    * Alertdialog that will be shown when you want to delete all the notes.
     * Dit is nu nog voor testing.
    */
    private fun popUpDialogDeleteAllNotes() {
        val builder = AlertDialog.Builder(this).apply {
            setMessage(R.string.delete_all_notes_message)
            setCancelable(true)
            setPositiveButton(R.string.yes) { dialog, id ->
                //TODO alleen al deleted notes verwijderen.
                noteViewModel.deleteAllDeletedNotes()
                dialog.cancel()
            }
            setNegativeButton(R.string.no) { dialog, id -> dialog.cancel() }
        }

        builder.create().show()
    }
}


