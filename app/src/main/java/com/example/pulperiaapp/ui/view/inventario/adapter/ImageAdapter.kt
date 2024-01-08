package com.example.pulperiaapp.ui.view.inventario.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.viewpager.widget.PagerAdapter
import com.example.pulperiaapp.R


class ImageAdapter(
    private val image: MutableList<Bitmap>,
    private val imageCounterListener: ImageCounterListener
) :
    PagerAdapter() {

    private var deletePosition: Int = -1


    // Se utiliza para crear y agregar vistas al ViewPager.
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)

        val itemView = inflater.inflate(R.layout.item_viewpaguer, container, false)
        val imageView = itemView.findViewById<ImageView>(R.id.image_view)
        val delete = itemView.findViewById<ImageView>(R.id.delete_button)
        itemView.tag = position

        val imagen = image[position]
        imageView.setImageBitmap(imagen)
        delete.isVisible = image.size != 3
        delete.setOnClickListener {
            onClickDelete(position)
        }

        container.addView(itemView)
        return itemView
    }

    // Dentro de la función onClickDelete en ImageAdapter
    private fun onClickDelete(position: Int) {
        if (position >= 0 && position < image.size) {
            deletePosition = position
            val deletedImage = image.removeAt(position)
            notifyDataSetChanged()
            val tamano = image.size
            imageCounterListener.onImageDelete(tamano, deletedImage)
        }
    }


    fun addImage(image: Bitmap) {
        // Si se eliminó una imagen, inserta la nueva imagen en esa posición
        if (deletePosition != -1) {
            this.image.add(deletePosition, image)
        } else {
            this.image.add(image)
        }

        notifyDataSetChanged()
        imageCounterListener.onImageAdd(this.image.size)
    }


    override fun getItemPosition(`object`: Any): Int {
        val view = `object` as View
        val position = view.tag as? Int ?: -1
        if (deletePosition != -1 && position >= deletePosition) {
            return POSITION_NONE
        }
        return position
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)

    }

    // Esta función debe devolver el número total de elementos o vistas que el ViewPager contendrá
    override fun getCount(): Int = image.size


    // Si la vista que está siendo mostrada actualmente es la misma que el objeto que se le está pasando.
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

}
