package com.example.quicknote

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var actionMode: ActionMode? = null
    private var notes: MutableList<Note> = ArrayList()
    private var noteAdapter: NoteAdapter? = null
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var items: ArrayList<String>
    lateinit var sharedPreferences: SharedPreferences
    var isClickPosition = -1
    var selectedItemPosition = -1
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        setSupportActionBar(toolbar)

        // Navigation Drawer
        drawerLayout = drawer_layout
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        // Shared Preferences
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)

        // Recyclerview opbouw
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = NoteAdapter(notes, applicationContext, object : OnClickListener {
                override fun onLongPressDelete(position: Int) {

                }

                override fun onClick(position: Int) {

                }
            }, object : InsertDeleteNote {
                override fun insertDeletedNote(position: Int, note: Note) {

                }
            })
        }

        //NoteViewModel
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        noteViewModel.allNotes.observe(this, Observer<MutableList<Note>> { notes ->
            this.notes = notes
            updateUI()
            if (notes.isEmpty()) {
                recyclerView.visibility = View.GONE
                snow_fall.visibility = View.GONE
                slideViewUp(linear_layout_no_notes)
            } else {
                recyclerView.visibility = View.VISIBLE
                snow_fall.visibility = View.VISIBLE
                linear_layout_no_notes.visibility = View.GONE
            }

            val isClicked = sharedPreferences.getInt(POSITION_SORTING, 6)
            sortingMethod(isClicked)
            noteAdapter?.noteListSearch()
        })

        fab.setOnClickListener {
            val intentAddNote = Intent(this, AddNote::class.java)
            startActivityForResult(intentAddNote, ADD_REQUEST_CODE)
        }
    }

    private fun slideViewUp(view: View) {
        view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val TRANSLATION_Y = view.height
                view.translationY = TRANSLATION_Y.toFloat()
                view.visibility = View.GONE
                view.animate()
                    .translationYBy((-TRANSLATION_Y).toFloat())
                    .setDuration(200)
                    .setStartDelay(0)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            view.visibility = View.VISIBLE
                        }
                    })
                    .start()
            }
        })
    }

    private fun updateUI() {
        if (noteAdapter == null) {
            noteAdapter = NoteAdapter(notes, applicationContext, object : OnClickListener {
                override fun onLongPressDelete(position: Int) {
                    noteViewModel.delete(notes[position])
                    notes.removeAt(position)
                    noteAdapter?.notifyItemRemoved(position)

                }

                override fun onClick(position: Int) {
                    val intentEditNote = Intent(applicationContext, EditNote::class.java).apply {
                        putExtra(SEND_DATA_EDIT_NOTE, position.let { notes[it] })
                    }
                    startActivityForResult(intentEditNote, EDIT_REQUEST_CODE)
                }
            }, object : InsertDeleteNote {
                override fun insertDeletedNote(position: Int, note: Note) {
                    noteViewModel.insert(note)
                }
            })
            recyclerView.adapter = noteAdapter
        } else {
            noteAdapter?.swapList(notes)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        hideIcon(R.id.action_share, menu)
        hideIcon(R.id.action_bijlage, menu)

        val searchItem = menu.findItem(R.id.action_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { noteAdapter?.filter(it) }
                    //noteAdapter?.filter?.filter(newText)
                    return false
                }
            })
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_delete_notes -> popUpDialogDeleteAllNotes()
            R.id.action_filter_list -> showFilterDialog()
        }

        return super.onOptionsItemSelected(item)
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

    private fun saveIsClickPreferences(isClickedPosition: Int) {
        isClickPosition = isClickedPosition
        sharedPreferences.edit().putInt(POSITION_SORTING, isClickedPosition).apply()

    }

    // Word misschien in de adapter klasse gestopt.
    private fun <T> sortingMethod(value: T) {
        if (value == 0 || value == getString(R.string.ascending)) {
            notes.sortWith(Comparator { o1, o2 -> o1.titleNote.toLowerCase(Locale.getDefault()).compareTo(o2.titleNote.toLowerCase(Locale.getDefault())) })
        } else if (value == 1 || value == getString(R.string.descending)) {
            notes.sortWith(Comparator { o1, o2 -> o2.titleNote.toLowerCase(Locale.getDefault()).compareTo(o1.titleNote.toLowerCase(Locale.getDefault())) })
        } else if (value == 2 || value == getString(R.string.date_newest)) {
            notes.sortWith(compareByDescending<Note> { it.dateNote }.thenBy { it.titleNote })
        } else {
            notes.sortWith(compareBy<Note> { it.dateNote }.thenBy { it.titleNote })
        }
    }

    private fun popUpDialogDeleteAllNotes() {
        val builder = AlertDialog.Builder(this).apply {
            setMessage(R.string.delete_all_notes_message)
            setCancelable(true)
            setPositiveButton(R.string.yes) { dialog, id ->
                noteViewModel.deleteAllNotes()
                dialog.cancel()
            }
            setNegativeButton(R.string.no) { dialog, id -> dialog.cancel() }
        }

        builder.create().show()
    }

    private fun showFilterDialog() {
        selectedItemPosition = sharedPreferences.getInt(SELECTED_ITEM_POSITION_DIALOG_SORTING, -1)
        val alertDialog = AlertDialog.Builder(this).apply {
            setTitle(R.string.sort_by)
            setIcon(R.drawable.ic_filter_list)
            setSingleChoiceItems(R.array.filter_notes, selectedItemPosition) { dialog, which ->
                sharedPreferences.edit().putInt(SELECTED_ITEM_POSITION_DIALOG_SORTING, which).apply()
                items = arrayListOf(getString(R.string.ascending), getString(R.string.descending),
                    getString(R.string.date_newest), getString(R.string.date_oldest))
                when (which) {
                    0 -> {
                        sortingMethod(items[which])
                        updateUI()
                        saveIsClickPreferences(which)
                    }
                    1 -> {
                        sortingMethod(items[which])
                        updateUI()
                        saveIsClickPreferences(which)
                    }
                    2 -> {
                        sortingMethod(items[which])
                        updateUI()
                        saveIsClickPreferences(which)
                    }
                    3 -> {
                        sortingMethod(items[which])
                        updateUI()
                        saveIsClickPreferences(which)
                    }
                }
            }
            setPositiveButton(R.string.sort) { dialog, _ -> dialog.cancel() }
            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
        }
        alertDialog.create().show()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_privacy -> {
                val intentPrivacy = Intent(this, PrivacyPolicyAcivity::class.java)
                startActivity(intentPrivacy)
            }

            R.id.nav_instellingen -> {}
            R.id.nav_notes -> {}
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
