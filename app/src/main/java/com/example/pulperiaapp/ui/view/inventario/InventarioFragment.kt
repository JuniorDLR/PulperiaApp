package com.example.pulperiaapp.ui.view.inventario

import ImageAdapter
import android.annotation.SuppressLint
import android.content.Context

import android.os.Bundle
import android.app.Activity
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import com.example.pulperiaapp.R
import com.example.pulperiaapp.data.database.entitie.InventarioEntity
import com.example.pulperiaapp.databinding.FragmentInventarioBinding
import com.example.pulperiaapp.domain.inventario.InventarioModel
import com.example.pulperiaapp.ui.view.inventario.adapter.ImageCounterListener
import com.example.pulperiaapp.ui.view.inventario.viewmodel.InventarioViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class InventarioFragment : Fragment() {

    private lateinit var binding: FragmentInventarioBinding
    private val inventarioModel: InventarioViewModel by viewModels()
    private lateinit var tableLayout: TableLayout
    private lateinit var tableRow: TableRow
    private val productoIngresado = mutableMapOf<String, List<InventarioModel>>()
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var viewPager: ViewPager
    private val MAX = 3
    private var imageToken = 0
    private val bitmapList = mutableListOf<Bitmap>()

    // Variables para las rutas de las imágenes
    private var ruta1: String? = null
    private var ruta2: String? = null
    private var ruta3: String? = null

    val galleryLaucnher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intentClip: Intent? = result.data
                if (intentClip != null) {
                    val clipData = intentClip.clipData
                    if (clipData != null) {
                        for (i in 0 until clipData.itemCount) {
                            val fromUri: Uri = clipData.getItemAt(i).uri
                            handleImageUri(fromUri)
                        }
                    } else {

                        val uri: Uri? = intentClip.data
                        if (uri != null) {
                            handleImageUri(uri)
                        }
                    }
                    actualizarConteo()
                }

            }
        }

    private fun handleImageUri(fromUri: Uri) {
        if (imageToken < MAX) {
            imageToken += 1

            val bitmap = getBitmapFromUri(fromUri)
            if (bitmap != null) {
                val bitmapImage = bitmapList.any { it.sameAs(bitmap) }
                if (!bitmapImage) {
                    when (imageToken) {
                        1 -> {
                            ruta1 = saveImageToInternalStorage(bitmap)
                            imageAdapter.addImage(bitmap)
                        }

                        2 -> {
                            ruta2 = saveImageToInternalStorage(bitmap)
                            imageAdapter.addImage(bitmap)
                        }

                        3 -> {
                            ruta3 = saveImageToInternalStorage(bitmap)
                            imageAdapter.addImage(bitmap)
                        }
                    }

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Esta imagen ya a sido seleccionada",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }


            } else {
                Toast.makeText(requireContext(), "No se puede cargar la imagen", Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Ya no se puede agregar mas imagenes",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String? {
        //Este objeto se utiliza para obtener acceso al almacenamiento interno del dispositivo.
        val wrapper = ContextWrapper(requireContext().applicationContext)
        var file: File? = null

        try {
            val dir = wrapper.getDir(
                "imagen",
                Context.MODE_PRIVATE
            ) // accedemos al directorio y le indicamos que solo la app pueda acceder  a el
            file = File(dir, "${System.currentTimeMillis()}.jpg")
            val stream: OutputStream =
                FileOutputStream(file) //Se crea un flujo de salida (OutputStream) que apunta al archivo recién creado.
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                100,
                stream
            )//Se utiliza el método compress del objeto Bitmap para comprimir la imagen en formato JPEG con calidad 100 y escribir los datos en el flujo de salida (stream).
            stream.flush() //Se vacía el búfer del flujo de salida para asegurarse de que todos los datos se han escrito en el archivo.
            stream.close()// Se cierra el flujo de salida para liberar recursos y finalizar la operación de escritura en el archivo.

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file?.absolutePath

    }

    @SuppressLint("Recycle")
    private fun getBitmapFromUri(fromUri: Uri): Bitmap? {
        return try {
            // Se utiliza para acceder a los datos de la Uri. Esto significa que se está abriendo un flujo para leer los datos de la imagen a la que apunta la Uri.
            val inputStream = requireContext().contentResolver.openInputStream(fromUri)
            BitmapFactory.decodeStream(inputStream) // Esta función convierte los datos de la imagen en el InputStream en un Bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentInventarioBinding.inflate(inflater, container, false)

        initComponent()
        actualizarConteo()
        return binding.root
    }

    private fun initComponent() {
        tableLayout = binding.tlProductoInventario
        tableRow = binding.trItem
        viewPager = binding.vpFoto

        imageAdapter = ImageAdapter(object : ImageCounterListener {
            override fun onImageAdd(imageCount: Int) {
                actualizarConteo()

            }

            override fun onImageDelete(imageCount: Int) {
                actualizarConteo()

            }

        })

        viewPager.adapter = imageAdapter
    }

    private var previousImageCount: Int = 0
    private fun actualizarConteo() {
        val conteo = imageAdapter.count
        val imageTex = "$conteo/$MAX"
        binding.contadorImage.text = imageTex
        binding.btnTomarFoto.visibility = if (conteo == MAX) View.GONE else View.VISIBLE


        if (conteo < previousImageCount) {
            Toast.makeText(requireContext(), "Imagen eliminada", Toast.LENGTH_LONG).show()
        }

        previousImageCount = conteo
        imageToken = conteo


    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAgregarTabla.setOnClickListener {
            agregarTabla()
        }
        binding.btnGuardar.setOnClickListener {
            guardarProducto()
        }
        binding.btnTomarFoto.setOnClickListener {
            openGalery()
        }

    }


    private fun openGalery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        galleryLaucnher.launch(intent)
    }


    private fun guardarProducto() {


        val fechaFormateada =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val listaInventario = mutableListOf<InventarioEntity>()
        if (productoIngresado.isNotEmpty()) {
            productoIngresado.forEach { (fecha, lista) ->
                for (prodcuto in lista) {
                    val nombre = prodcuto.nombre
                    val tamano = prodcuto.tamano
                    val cantidadCajilla = prodcuto.cantidad_cajilla
                    val cantidad = prodcuto.cantidad
                    val importe = prodcuto.importe


                    val inventario = InventarioEntity(
                        id = 0,
                        nombreProducto = nombre,
                        tamano = tamano,
                        fechaEntrega = fechaFormateada,
                        cantidadCajilla = cantidadCajilla,
                        cantidad = cantidad,
                        precio = importe,
                        ruta1 = ruta1,
                        ruta2 = ruta2,
                        ruta3 = ruta3

                    )

                    listaInventario.add(inventario)
                }

            }
            inventarioModel.insertarInventario(listaInventario)

            requireActivity().supportFragmentManager.popBackStack()
            Toast.makeText(
                requireContext(), "Inventario Guardado exitosamente!!", Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                requireContext(), "Los campos estan vacios", Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun agregarTabla() {
        val nombreProducto = binding.tvNombreProducto.text.toString()
        val tamano = binding.tvTamanoEnvase.text.toString()
        val cantidadCajilla = binding.tvCantidadPorCajilla.text.toString()
        val cantidad = binding.tvCantidadCajillas.text.toString()
        val precioUnitario = binding.tvPrecioUnitario.text.toString()
        val fechaFormateada =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val camposVacios = mutableListOf<TextInputEditText>()

        if (nombreProducto.isEmpty()) {
            binding.tvNombreProducto.error = "Campo vacío"
            camposVacios.add(binding.tvNombreProducto)
        } else {
            binding.tvNombreProducto.error = null
        }

        if (tamano.isEmpty()) {
            binding.tvTamanoEnvase.error = "Campo vacío"
            camposVacios.add(binding.tvTamanoEnvase)
        } else {
            binding.tvTamanoEnvase.error = null
        }

        if (cantidadCajilla.isEmpty()) {
            binding.tvCantidadPorCajilla.error = "Campo vacío"
            camposVacios.add(binding.tvCantidadPorCajilla)
        } else {
            binding.tvCantidadPorCajilla.error = null
        }

        if (cantidad.isEmpty()) {
            binding.tvCantidadCajillas.error = "Campo vacío"
            camposVacios.add(binding.tvCantidadCajillas)
        } else {
            binding.tvCantidadCajillas.error = null
        }

        if (precioUnitario.isEmpty()) {
            binding.tvPrecioUnitario.error = "Campo vacío"
            camposVacios.add(binding.tvPrecioUnitario)
        } else {
            binding.tvPrecioUnitario.error = null
        }

        // Si hay campos vacíos, enfoca el primer campo vacío.
        if (camposVacios.isNotEmpty()) {
            val primerCampoVacio = camposVacios[0]
            primerCampoVacio.requestFocus()
        } else {
            val precioDouble = precioUnitario.toDouble()
            val cantidadInt = cantidad.toInt()


            val inventario = InventarioModel(
                0,
                nombreProducto,
                tamano,
                fechaFormateada,
                cantidadCajilla,
                cantidadInt,
                precioDouble
            )

            val listaInventario: MutableList<InventarioModel> =
                productoIngresado[fechaFormateada]?.toMutableList() ?: mutableListOf()
            listaInventario.add(inventario)

            productoIngresado[fechaFormateada] = listaInventario
            limpiarTexto()
            actualizarTabla()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun actualizarTabla() {
        var precioSuperTotal = 0.0
        tableLayout.removeAllViews()
        for ((fecha, listaInventario) in productoIngresado) {

            for (lista in listaInventario) {
                val nombre = lista.nombre
                val tamano = lista.tamano
                val cantidadCajilla = lista.cantidad_cajilla
                val cantidad = lista.cantidad
                val precio = lista.importe


                val tableRow = LayoutInflater.from(requireContext())
                    .inflate(R.layout.table_row_inventario, null) as TableRow

                val nombreView = tableRow.findViewById<TextView>(R.id.tvProductoRow)
                nombreView.text = nombre

                val tamanoView = tableRow.findViewById<TextView>(R.id.tvTamanoR)
                tamanoView.text = tamano

                val cantidadCajillaView = tableRow.findViewById<TextView>(R.id.tvCajillaR)
                cantidadCajillaView.text = cantidadCajilla

                val cantidadView = tableRow.findViewById<TextView>(R.id.tvCantidadR)
                cantidadView.text = cantidad.toString()


                tableLayout.addView(tableRow)
                precioSuperTotal += precio

            }

        }
        binding.tvPrecioPagar.text = "Precio Total: $precioSuperTotal"
    }

    fun limpiarTexto() {
        binding.tvNombreProducto.setText("")
        binding.tvTamanoEnvase.setText("")
        binding.tvCantidadPorCajilla.setText("")
        binding.tvCantidadCajillas.setText("")
        binding.tvPrecioUnitario.setText("")

    }

}


