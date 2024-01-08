package com.example.pulperiaapp.ui.view.inventario.view


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.app.Activity
import android.app.AlertDialog
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.getSystemService
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.example.pulperiaapp.R
import com.example.pulperiaapp.data.database.entitie.InventarioEntity
import com.example.pulperiaapp.data.database.entitie.InventarioFotoEntity
import com.example.pulperiaapp.databinding.FragmentInventarioBinding
import com.example.pulperiaapp.domain.inventario.InventarioModel
import com.example.pulperiaapp.ui.view.inventario.adapter.ImageAdapter
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
import java.util.UUID


@AndroidEntryPoint
class InventarioFragment : Fragment() {

    private lateinit var binding: FragmentInventarioBinding
    private val inventarioModel: InventarioViewModel by viewModels()
    private lateinit var tableLayout: TableLayout
    private lateinit var tableRow: TableRow
    private val productoIngresado = mutableMapOf<String, MutableList<InventarioModel>>()
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var viewPager: ViewPager
    private val MAX = 3
    private var imageToken = 0
    private val bitmapList = mutableListOf<Bitmap>()
    private var productoSeleccionado: Int = 0
    private var ultimoId = 1
    private val key = "Producto"


    // Variables para las rutas de las imágenes
    private var ruta1: String? = null
    private var ruta2: String? = null
    private var ruta3: String? = null
    private var esEdicion: Boolean = false

    private val galleryLaucnher =
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
                        requireContext(), "Esta imagen ya a sido seleccionada", Toast.LENGTH_LONG
                    ).show()
                }


            } else {
                Toast.makeText(requireContext(), "No se puede cargar la imagen", Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            Toast.makeText(
                requireContext(), "Ya no se puede agregar mas imagenes", Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String? {
        //Este objeto se utiliza para obtener acceso al almacenamiento interno del dispositivo.
        val wrapper = ContextWrapper(requireContext().applicationContext)
        var file: File? = null

        try {
            val dir = wrapper.getDir(
                "imagen", Context.MODE_PRIVATE
            ) // accedemos al directorio y le indicamos que solo la app pueda acceder  a el
            file = File(dir, "${System.currentTimeMillis()}.jpg")
            val stream: OutputStream =
                FileOutputStream(file) //Se crea un flujo de salida (OutputStream) que apunta al archivo recién creado.
            bitmap.compress(
                Bitmap.CompressFormat.JPEG, 100, stream
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

        imageAdapter = ImageAdapter(bitmapList, object : ImageCounterListener {
            override fun onImageAdd(imageCount: Int) {
                actualizarConteo()

            }

            override fun onImageDelete(imageCount: Int, deletedImage: Bitmap?) {
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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_inventarioFragment_to_inventarioDatosFragment)
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

        if (productoIngresado.isNotEmpty()) {

            val idFoto = UUID.randomUUID().toString()
            val listaInventarioFotos = listOf(InventarioFotoEntity(0, idFoto, ruta1, ruta2, ruta3))
            inventarioModel.insertarFoto(listaInventarioFotos)

            for (entry in productoIngresado) {
                val (_, listaFotos) = entry

                // Insertar el producto en tbl_inventario
                for (producto in listaFotos) {
                    val nombre = producto.nombreProducto
                    val tamano = producto.tamano
                    val cantidadCajilla = producto.cantidadCajilla
                    val cantidad = producto.cantidad
                    val importe = producto.importe

                    val inventario = InventarioEntity(
                        id = 0,
                        nombreProducto = nombre,
                        tamano = tamano,
                        fechaEntrega = fechaFormateada,
                        cantidadCajilla = cantidadCajilla,
                        cantidad = cantidad,
                        precio = importe,
                        idFotos = idFoto
                    )

                    inventarioModel.insertarInventario(inventario)
                }
            }

            val action =
                InventarioFragmentDirections.actionInventarioFragmentToInventarioDatosFragment()
            Navigation.findNavController(binding.root).navigate(action)
            Toast.makeText(
                requireContext(), "Inventario Guardado exitosamente!!", Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(requireContext(), "Los campos están vacíos", Toast.LENGTH_LONG).show()
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

            if (esEdicion) {


                val precioDouble = precioUnitario.toDouble()
                val cantidadInt = cantidad.toInt()
                val cantidadCajillaInt = cantidadCajilla.toInt()


                productoIngresado[key]?.find { it.id == productoSeleccionado }?.apply {
                    this.nombreProducto = nombreProducto
                    this.tamano = tamano
                    this.cantidadCajilla = cantidadCajillaInt
                    this.cantidad = cantidadInt
                    this.importe = precioDouble
                    cleanText()
                    actualizarTabla()
                }
                esEdicion = false

            } else {
                val idFotos = UUID.randomUUID().toString()
                val nuevoId = ultimoId++
                val precioDouble = precioUnitario.toDouble()
                val cantidadInt = cantidad.toInt()

                val cantidadCajillaInt = cantidadCajilla.toInt()

                val inventario = InventarioModel(
                    nuevoId,
                    nombreProducto,
                    tamano,
                    fechaFormateada,
                    null,
                    cantidadCajillaInt,
                    cantidadInt,
                    precioDouble,
                    idFotos
                )

                val listaInventario: MutableList<InventarioModel> =
                    productoIngresado[key]?.toMutableList() ?: mutableListOf()
                listaInventario.add(inventario)

                productoIngresado[key] = listaInventario
                cleanText()
                actualizarTabla()
            }

        }
        ocultarTeclado()
    }

    private fun ocultarTeclado() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().window.decorView.windowToken, 0)
    }


    @SuppressLint("SetTextI18n", "InflateParams")
    private fun actualizarTabla() {
        tableLayout.removeAllViews()
        var precioSuperTotal = 0.0
        productoIngresado[key]?.forEach { lista ->
            precioSuperTotal += lista.importe
            visualizarTabla(lista)

        }
        binding.tvPrecioPagar.text = "Precio Total: $precioSuperTotal"
    }


    @SuppressLint("InflateParams")
    private fun visualizarTabla(inventarioModel: InventarioModel) {
        val tableRow = LayoutInflater.from(requireContext())
            .inflate(R.layout.table_row_inventario, null) as TableRow


        val nombreView = tableRow.findViewById<TextView>(R.id.tvProductoRow)
        nombreView.text = inventarioModel.nombreProducto

        val tamanoView = tableRow.findViewById<TextView>(R.id.tvTamanoR)
        tamanoView.text = inventarioModel.tamano

        val cantidadCajillaView = tableRow.findViewById<TextView>(R.id.tvCajillaR)
        cantidadCajillaView.text = inventarioModel.cantidadCajilla.toString()

        val cantidadView = tableRow.findViewById<TextView>(R.id.tvCantidadR)
        cantidadView.text = inventarioModel.cantidad.toString()

        val opciones = arrayOf("Editar", "Eliminar")

        nombreView.setOnClickListener {
            productoSeleccionado = inventarioModel.id


            val alert = AlertDialog.Builder(requireContext()).setTitle("Eligue una opcion")
                .setItems(opciones) { _, switch ->
                    when (switch) {
                        0 -> {

                            editarTabla(inventarioModel)
                        }

                        1 -> {

                            eliminarTabla(inventarioModel)
                        }
                    }
                }
            alert.show()
        }


        tableLayout.addView(tableRow)
    }

    private fun eliminarTabla(idExistente: InventarioModel?) {
        idExistente?.let {
            productoIngresado[key]?.remove(it)
        }
        actualizarTabla()

    }

    private fun editarTabla(idExistente: InventarioModel?) {
        esEdicion = true
        idExistente?.let {
            binding.tvNombreProducto.setText(it.nombreProducto)
            binding.tvTamanoEnvase.setText(it.tamano)
            binding.tvCantidadPorCajilla.setText(it.cantidadCajilla.toString())
            binding.tvCantidadCajillas.setText(it.cantidad.toString())
            binding.tvPrecioUnitario.setText(it.importe.toString())
        }

    }


    private fun cleanText() {
        binding.tvNombreProducto.setText("")
        binding.tvTamanoEnvase.setText("")
        binding.tvCantidadPorCajilla.setText("")
        binding.tvCantidadCajillas.setText("")
        binding.tvPrecioUnitario.setText("")

    }

}


