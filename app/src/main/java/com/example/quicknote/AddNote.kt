package com.example.quicknote

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
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
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.android.synthetic.main.content_add_note.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddNote : AppCompatActivity() {

    var imageUri: Uri? = null
    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        val currentDate: String =
            SimpleDateFormat(getString(R.string.date_format), Locale.getDefault()).format(Date())

        fab.setOnClickListener {
            val note = Note(0, title_add_note.text.toString(), description_add_note.text.toString(), currentDate, imageToByteArray(image_note) )
            if (note.titleNote.isEmpty()) {
                Toast.makeText(this, R.string.title_can_not_be_empty, Toast.LENGTH_SHORT).show()
            } else {
                val intentSendData = Intent().apply {
                    putExtra(SEND_NOTE_DATA, note)
                }

                Toast.makeText(this, R.string.note_added, Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK, intentSendData)
            }
        }

        registerForContextMenu(image_note)

        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_image, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_image -> {
                deleteImage(image_note, applicationContext)
                imageUri = null
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun checkAPIAppVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE_IMAGE_GALERY)
            } else {
                pickImageFromGalery(this)
            }
        } else {
            // System OS is lower than m
            if (PermissionChecker.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PermissionChecker.PERMISSION_DENIED) {
                val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE_IMAGE_GALERY)
            } else {
                pickImageFromGalery(this)
            }
        }
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE_GALERY) {
            setImageToView(this, data, image_note)
            imageUri = data?.data
        }

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_CODE_CAMERA) {
            image_note.visibility = View.VISIBLE
            Glide.with(this).load(currentPhotoPath).into(image_note)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        hideIcon(R.id.action_search, menu)
        hideIcon(R.id.action_delete_notes, menu)
        hideIcon(R.id.action_filter_list, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_share -> {
                if (imageUri != null) {
                    shareImageFromUri(imageUri, this, title_add_note.text.toString(), description_add_note.text.toString())
                } else {
                    shareData(title_add_note.text.toString(), description_add_note.text.toString(), this)
                }
                return true
            }
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.gallery_foto -> {
                checkAPIAppVersion()
                return true
            }
            R.id.camera_foto -> {
                checkAPIAppVersionCamera()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
