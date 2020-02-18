package com.example.quicknote

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.*

const val ADD_REQUEST_CODE = 1
const val SEND_NOTE_DATA = "sendNoteData"
const val EDIT_REQUEST_CODE = 2
const val SEND_DATA_EDIT_NOTE = "sendDataEditNote"
const val SEND_EDITED_NOTE = "sendEditedNote"
const val POSITION_SORTING = "position_sorting"
const val SHARED_PREFERENCES = "shared_preferences"
const val SELECTED_ITEM_POSITION_DIALOG_SORTING = "selected_dialog_item_position"
const val IMAGE_PICK_CODE_GALERY = 1000
const val PERMISSION_CODE_IMAGE_GALERY = 1001

fun shareData(titleNote: String, descriptionNote: String, context: Context) {
    val sendIntent: Intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_SUBJECT, "Look at my note!")
        putExtra(Intent.EXTRA_TEXT, "$titleNote \n\n$descriptionNote")
        type = "text/plain"
    }

    context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.deel_note)))
}

fun shareImageFromUri(imageUri: Uri?, context: Context, titleNote: String, descriptionNote: String) {
    Picasso.get().load(imageUri).into(object : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_SUBJECT, "Look at my note!")
                putExtra(Intent.EXTRA_TEXT, "$titleNote \n\n$descriptionNote")
                putExtra(Intent.EXTRA_STREAM, getBitmapFromView(bitmap, context))
            }
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.deel_note)))
        }

    })
}

fun getBitmapFromView(bmp: Bitmap?, context: Context): Uri? {
    var bmpUri: Uri? = null
    try {
        val file = File(context.externalCacheDir, System.currentTimeMillis().toString() + ".jpg")

        val out = FileOutputStream(file)
        bmp?.compress(Bitmap.CompressFormat.JPEG, 90, out)
        out.close()
        bmpUri = Uri.fromFile(file)

    } catch (e: IOException) {
        e.printStackTrace()
    }
    return bmpUri
}
fun hideIcon(id: Int, menu: Menu) {
    menu.findItem(id).apply {
        isVisible = false
    }
}

fun setImageToView(context: Context, data: Intent?, image_note: ImageView) {
    image_note.apply {
        visibility = View.VISIBLE

        try {
            val imageUri = data?.data
            if (imageUri != null) {
                val imageStream = context.contentResolver.openInputStream(imageUri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                setImageBitmap(selectedImage)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
}

fun pickImageFromGalery(fragmentActivity: FragmentActivity) {
    val intent = Intent(Intent.ACTION_PICK).apply {
        type = "image/*"
    }
    fragmentActivity.startActivityForResult(intent, IMAGE_PICK_CODE_GALERY)
}

fun deleteImage(image_note: ImageView, context: Context) {
    if (image_note.drawable == null) {
        Log.d("Image", "Image is null.")
    } else {
        image_note.apply {
            setImageBitmap(null)
            visibility = View.GONE
        }
        Toast.makeText(context, "Image deleted", Toast.LENGTH_SHORT).show()
    }
}

fun imageToByteArray(image_note: ImageView): ByteArray {
    val bitmap: Bitmap? = (image_note.drawable as BitmapDrawable?)?.bitmap
    val baos = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)

    return baos.toByteArray()
}