package com.example.pulperiaapp.ui.view.inventario.adapter

import android.graphics.Bitmap

interface ImageCounterListener {
    fun onImageAdd(imageCount: Int)
    fun onImageDelete(imageCount: Int, deletedImage: Bitmap?)
}
