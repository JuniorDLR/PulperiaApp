package com.example.pulperiaapp.ui.view.inventario.adapter

import android.graphics.Bitmap
import android.widget.ImageView

interface ImageCounterListener {
    fun onImageAdd(imageCount: Int)
    fun onImageDelete(imageCount: Int, deletedImage: Bitmap?)
    fun ImageDeleteListener(position: Int)
    fun esEdicion(eliminar: ImageView)
}
