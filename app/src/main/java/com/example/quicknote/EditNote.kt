package com.example.quicknote

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_edit_note.*
import kotlinx.android.synthetic.main.content_edit_note.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditNote : AppCompatActivity() {

    var imageUri: Uri? = null
    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)
        setSupportActionBar(toolbar)

        // Add back arrow to toolbar.
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        val note = intent.getParcelableExtra<Note?>(SEND_DATA_EDIT_NOTE)
        if (note != null) {
            title_edit_note.setText(note.titleNote)
            description_edit_note.setText(note.descriptionNote)
        }

        val currentDate: String =
            SimpleDateFormat(getString(R.string.date_format), Locale.getDefault()).format(Date())

        if (note != null) {
            // Set received image from database to imageView.
            if (note.imageUriNote?.isNotEmpty()!!) {
                val bitmap =
                    BitmapFactory.decodeByteArray(note.imageUriNote, 0, note.imageUriNote!!.size)

                val pathFileImage = getFile(bitmap, this)

                // Returns een uri van de current Path
                MediaScannerConnection.scanFile(this, arrayOf(pathFileImage), null) { path, uri ->
                    imageUri = uri
                }

                image_note_edit.apply {
                    visibility = View.VISIBLE
                    setImageBitmap(bitmap)
                }
            } else {
                Log.d("ImageNull", "Image size is null")
            }
        }

        // Send data to mainactivity.
        fab.setOnClickListener {
            note?.apply {
                titleNote = title_edit_note.text.toString()
                descriptionNote = description_edit_note.text.toString()
                dateNote = currentDate
                imageUriNote = imageToByteArray(image_note_edit)
            }

            if (note != null) {
                if (note.titleNote.isEmpty()) {
                    Toast.makeText(this, R.string.title_can_not_be_empty, Toast.LENGTH_SHORT).show()
                } else {
                    val editIntent = Intent().apply {
                        putExtra(SEND_EDITED_NOTE, note)
                    }

                    Toast.makeText(this, R.string.note_edited, Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK, editIntent)
                }
            }
        }

        registerForContextMenu(image_note_edit)
    }

    /**
     * Returns the path of the image file.
     *
     * @param bmp This is the bitmap image.
     * @param context
     */
    fun getFile(bmp: Bitmap?, context: Context): String {
        val file = File(context.externalCacheDir, System.currentTimeMillis().toString() + ".jpg")

        val out = FileOutputStream(file)
        bmp?.compress(Bitmap.CompressFormat.JPEG, 90, out)
        out.close()

        return file.absolutePath
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_image, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_image -> {
                deleteImage(image_note_edit, applicationContext)
                imageUri = null
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        hideIcon(R.id.action_search, menu)
        hideIcon(R.id.action_delete_notes, menu)
        hideIcon(R.id.action_filter_list, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle clicks on toolbar.
        when(item.itemId) {
            R.id.action_share -> {
                if (imageUri != null) {
                    shareImageFromUri(imageUri, this, title_edit_note.text.toString(), description_edit_note.text.toString())
                } else {
                    shareData(title_edit_note.text.toString(), description_edit_note.text.toString(), this)
                }
                return true
            }
            android.R.id.home -> {
                finish() // Close this activity and return to preview activity
                return true
            }
            R.id.gallery_foto -> {
                checkAPIAppVersionGalery(applicationContext, this, this)
                return true
            }
            R.id.camera_foto -> {
                val packageManager: PackageManager = packageManager
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    checkAPIAppVersionCamera()
                } else {
                    Toast.makeText(this, "You don't have a camera app.", Toast.LENGTH_SHORT).show()
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE_GALERY) {
            setImageToView(this, data, image_note_edit)
            imageUri = data?.data
        }

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_CODE_CAMERA) {
            image_note_edit.visibility = View.VISIBLE
            Glide.with(this).load(currentPhotoPath).into(image_note_edit)

            // Returns een uri van de current Path
            MediaScannerConnection.scanFile(this, arrayOf(currentPhotoPath), null) { path, uri ->
                imageUri = uri
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE_IMAGE_GALERY -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGalery(this)
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
            }

            PERMISSION_CODE_IMAGE_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Check the api app version when you gonna use the camera. After that it checks if you already give
     * permission to read your camera.
     */
    private fun checkAPIAppVersionCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(android.Manifest.permission.CAMERA)
                requestPermissions(permissions, PERMISSION_CODE_IMAGE_CAMERA)
            } else {
                openCamera()
            }
        } else {
            // System OS is lower than m
            if (PermissionChecker.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PermissionChecker.PERMISSION_DENIED || PermissionChecker.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PermissionChecker.PERMISSION_DENIED) {
                val permissions = arrayOf(android.Manifest.permission.CAMERA)
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE_IMAGE_CAMERA)
            } else {
                openCamera()
            }
        }
    }

    /**
     * Open up the camera app on your phone.
     */
    fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Zorgt ervoor dat er een camera activity is die de intent aan kan
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Maakt de file aan waar de foto heen gaat
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }

                // Word geactiveerd als er een file is aan gemaakt.
                photoFile?.also {
                    val photoUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, IMAGE_CODE_CAMERA)
                }
            }
        }
    }

    /**
     * Create a image file.
     */
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
}
