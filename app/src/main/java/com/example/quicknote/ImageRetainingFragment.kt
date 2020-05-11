package com.example.quicknote

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment

class ImageRetainingFragment : Fragment() {

    lateinit var image: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retain this fragmment
        retainInstance = true
    }

    fun setImageBitmap(selectedImage: Bitmap) {
        image = selectedImage
    }

    fun getImageBitmap(): Bitmap {
        return image
    }

}