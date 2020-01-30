package com.example.quicknote

import android.content.Context
import android.content.Intent
import android.view.Menu

const val ADD_REQUEST_CODE = 1
const val SEND_NOTE_DATA = "sendNoteData"
const val EDIT_REQUEST_CODE = 2
const val SEND_DATA_EDIT_NOTE = "sendDataEditNote"
const val SEND_EDITED_NOTE = "sendEditedNote"

fun shareData(titleNote: String, descriptionNote: String, context: Context) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_SUBJECT, titleNote)
        putExtra(Intent.EXTRA_TEXT, descriptionNote)
        type = "text/plain"
    }

    context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.deel_note)))
}

fun hideIcon(id: Int, menu: Menu) {
    menu.findItem(id).apply {
        isVisible = false
    }
}