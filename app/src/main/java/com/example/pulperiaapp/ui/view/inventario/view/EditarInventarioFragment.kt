package com.example.pulperiaapp.ui.view.inventario.view

import ImageAdapter
import android.annotation.SuppressLint
import android.app.Activity.*
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.example.pulperiaapp.R
import com.example.pulperiaapp.data.database.entitie.InventarioEntity
import com.example.pulperiaapp.databinding.FragmentEditarInventarioBinding
import com.example.pulperiaapp.domain.inventario.InventarioModel
import com.example.pulperiaapp.ui.view.inventario.adapter.ImageCounterListener
import com.example.pulperiaapp.ui.view.inventario.viewmodel.InventarioViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class EditarInventarioFragment : Fragment() {

    private lateinit var binding: FragmentEditarInventarioBinding
    private val inventarioModel: InventarioViewModel by viewModels()
    private val args: EditarInventarioFragmentArgs by navArgs()
    private lateinit var tableLayout: TableLayout
    private var productoSeleccionado: Int = 0
    private lateinit var viewPager: ViewPager
    private val inventarioRecuperado = mutableMapOf<String, MutableList<InventarioModel>>()
    private lateinit var imageAdapter: ImageAdapter
    private var ruta1Editar: String? = null
    private var ruta2Editar: String? = null
    private var ruta3Editar: String? = null
    private val MAX = 3
    private var imageToken = 0
    private val bitmapList = mutableListOf<Bitmap>()


    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {
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
                    actualizarConteo(imageAdapter.count)
                }
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditarInventarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idInventario = args.idInventario
        inventarioModel.inventarioModel.observe(viewLifecycleOwner) { lista ->
            tableLayout.removeAllViews()
            lista.forEach { (i, item) ->
                item.forEach { j ->
                    agregarFila(j)
                }
            }
        }

        initComponent()
        initData(idInventario)

        binding.btnTablaEditar.setOnClickListener {
            editarTabla()
        }

        binding.btnTomarFotoEditar.setOnClickListener {
            openGallery()
        }

        binding.btnGuardar.setOnClickListener {
            guardarInventarioEditado()
        }
    }

    private fun guardarInventarioEditado() {
        val idInventario = args.idInventario
        val totalRegistros = inventarioRecuperado[idInventario]?.size ?: 0

        val cantidadMinimaImagenes = 3
        if ((imageAdapter.getItemCount() < cantidadMinimaImagenes)) {

            Toast.makeText(requireContext(), "Debes de ingresar 3 imaganes", Toast.LENGTH_LONG)
                .show()
            return
        } else {
            inventarioRecuperado[idInventario]?.forEachIndexed { index, lista ->
                val id = lista.id
                val nombre = lista.nombre_producto
                val tamano = lista.tamano
                val fecha = lista.fecha_entrega
                val fechaEditada =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val cantidadCajilla = lista.cantidad_cajilla
                val cantidad = lista.cantidad
                val importe = lista.importe


                val imagen1: String? = if (index == totalRegistros - 1) {
                    if (!imageAdapter.hasImageAtPosition(0)) {
                        null
                    } else {
                        lista.imagen1
                    }
                } else {
                    null
                }

                val imagen2: String? = if (index == totalRegistros - 1) {
                    if (!imageAdapter.hasImageAtPosition(1)) {
                        null
                    } else {
                        lista.imagen2
                    }
                } else {
                    null
                }

                val imagen3: String? = if (index == totalRegistros - 1) {
                    if (!imageAdapter.hasImageAtPosition(2)) {
                        null
                    } else {
                        lista.imagen3
                    }
                } else {
                    null
                }
                val entity = InventarioEntity(
                    id,
                    nombre,
                    tamano,
                    fecha,
                    fechaEditada,
                    cantidadCajilla,
                    cantidad,
                    importe,
                    imagen1,
                    imagen2,
                    imagen3
                )

                lifecycleScope.launch {
                    inventarioModel.editarInventario(entity)
                }
            }

            requireActivity().supportFragmentManager.popBackStack()
            Toast.makeText(requireContext(), "Datos editados exitosamente", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun editarTabla() {
        lifecycleScope.launch {
            val idInventario = args.idInventario
            val nombre = binding.tvNombreProductoEditar.text.toString()
            val tamano = binding.tvTamanoEnvaseEditar.text.toString()
            val cantidad = binding.tvCantidadCajillasEditar.text.toString()
            val cantidadCajilla = binding.tvCantidadPorCajillaEditar.text.toString()
            val import = binding.tvPrecioUnitarioEditar.text.toString()
            val campoVacio = mutableListOf<TextInputEditText>()

            if (nombre.isEmpty()) {
                binding.tvNombreProductoEditar.error = "Campo vacio"
                campoVacio.add(binding.tvNombreProductoEditar)
            } else {
                binding.tvNombreProductoEditar.error = null
            }


            if (tamano.isEmpty()) {
                binding.tvTamanoEnvaseEditar.error = "Campo vacio"
                campoVacio.add(binding.tvTamanoEnvaseEditar)
            } else {
                binding.tvTamanoEnvaseEditar.error = null
            }



            if (cantidadCajilla.isEmpty()) {
                binding.tvCantidadPorCajillaEditar.error = "Campo vacio"
                campoVacio.add(binding.tvCantidadPorCajillaEditar)
            } else {
                binding.tvCantidadPorCajillaEditar.error = null
            }

            if (cantidad.isEmpty()) {
                binding.tvCantidadCajillasEditar.error = "Campo vacio"
                campoVacio.add(binding.tvCantidadCajillasEditar)
            } else {
                binding.tvCantidadCajillasEditar.error = null
            }


            if (import.isEmpty()) {
                binding.tvPrecioUnitarioEditar.error = "Campo vacio"
                campoVacio.add(binding.tvPrecioUnitarioEditar)
            } else {
                binding.tvPrecioPagar.error = null
            }

            if (campoVacio.isNotEmpty()) {
                val enfocarCampo = campoVacio[0]
                enfocarCampo.requestFocus()
            } else {
                inventarioRecuperado[idInventario]?.find { it.id == productoSeleccionado }?.apply {
                    this.importe = import.toDouble()
                    this.nombre_producto = nombre
                    this.tamano = tamano
                    this.cantidad = cantidad.toInt()
                    this.cantidad_cajilla = cantidadCajilla.toInt()


                    actulizarTabla()
                }
            }


        }

    }


    private fun initComponent() {
        tableLayout = binding.tlProductoInventario
        imageAdapter = ImageAdapter(bitmapList, object : ImageCounterListener {
            override fun onImageAdd(imageCount: Int) {
                actualizarConteo(imageCount)
            }

            override fun onImageDelete(imageCount: Int) {
                actualizarConteo(imageCount)

            }

        })

        viewPager = binding.vpFotoEditar
        viewPager.adapter = imageAdapter
    }


    private fun actualizarConteo(imageCount: Int) {
        val count = "$imageCount/$MAX"
        binding.contadorImage.text = count


        if (imageCount < MAX) {
            Toast.makeText(requireContext(), "Imagen eliminada", Toast.LENGTH_SHORT).show()
        }

        binding.btnTomarFotoEditar.visibility = if (imageCount < MAX) View.VISIBLE else View.GONE
        imageToken = imageCount

    }


    private fun initData(idInventario: String) {
        lifecycleScope.launch {
            inventarioModel.obtenerDetalleInventario(idInventario)
            inventarioModel.groupInventario.observe(viewLifecycleOwner) { lista ->
                lista?.let {
                    it.forEach { j ->
                        Log.d("lista", lista.toString())
                        guardarDatosRecuperados(j, idInventario)
                    }

                    actulizarTabla()
                }
            }
        }
    }

    private fun guardarDatosRecuperados(inventario: InventarioModel, idFecha: String) {
        if (inventario.cantidad > 0) {

            inventarioRecuperado.getOrPut(idFecha) { mutableListOf() }.add(inventario)
        }
    }

    private fun actulizarTabla() {
        tableLayout.removeAllViews()
        val idCliente = args.idInventario
        var total = 0.0
        bitmapList.clear()
        inventarioRecuperado[idCliente]?.forEach { inventario ->
            total += inventario.importe

            agregarFila(inventario)
        }
        val count = "${imageAdapter.count}/$MAX"
        binding.tvPrecioPagar.text = total.toString()
        binding.contadorImage.text = count

    }

    @SuppressLint("InflateParams")
    private fun agregarFila(inventario: InventarioModel) {
        val table = LayoutInflater.from(requireContext())
            .inflate(R.layout.table_row_inventario, null) as TableRow


        val nombreView = table.findViewById<TextView>(R.id.tvProductoRow)
        nombreView.text = inventario.nombre_producto

        val tamanoView = table.findViewById<TextView>(R.id.tvTamanoR)
        tamanoView.text = inventario.tamano

        val cantidadView = table.findViewById<TextView>(R.id.tvCantidadR)
        cantidadView.text = inventario.cantidad.toString()

        val cantidadCajillaView = table.findViewById<TextView>(R.id.tvCajillaR)
        cantidadCajillaView.text = inventario.cantidad_cajilla.toString()


        ruta1Editar = inventario.imagen1
        ruta2Editar = inventario.imagen2
        ruta3Editar = inventario.imagen3

        if (!ruta1Editar.isNullOrEmpty()) {
            val bitmap1 = BitmapFactory.decodeFile(ruta1Editar)
            if (bitmap1 != null) {
                bitmapList.add(bitmap1)
            }
        }

        if (!ruta2Editar.isNullOrEmpty()) {
            val bitmap2 = BitmapFactory.decodeFile(ruta2Editar)
            if (bitmap2 != null) {
                bitmapList.add(bitmap2)
            }
        }

        if (!ruta3Editar.isNullOrEmpty()) {
            val bitmap3 = BitmapFactory.decodeFile(ruta3Editar)
            if (bitmap3 != null) {
                bitmapList.add(bitmap3)
            }
        }

        imageAdapter.notifyDataSetChanged()
        binding.btnTomarFotoEditar.visibility =
            if (imageAdapter.count == MAX) View.GONE else View.VISIBLE


        nombreView.setOnClickListener {
            productoSeleccionado = inventario.id
            binding.tvNombreProductoEditar.setText(inventario.nombre_producto)
            binding.tvTamanoEnvaseEditar.setText(inventario.tamano)
            binding.tvCantidadCajillasEditar.setText(inventario.cantidad.toString())
            binding.tvCantidadPorCajillaEditar.setText(inventario.cantidad_cajilla.toString())
            binding.tvPrecioUnitarioEditar.setText(inventario.importe.toString())
        }

        tableLayout.addView(table)
    }


    fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        galleryLauncher.launch(intent)
    }

    private fun handleImageUri(fromUri: Uri) {
        if (imageToken < MAX) {
            imageToken += 1

            val bitmap = getBitmapFromUri(fromUri)
            if (bitmap != null) {
                val bitmapUri = bitmapList.any { it.sameAs(bitmap) }
                val posicion = imageAdapter.getDeletedPosition()
                if (!bitmapUri) {
                    when (imageToken) {
                        1 -> {
                            ruta1Editar = saveImageToInternalStorage(bitmap)

                        }

                        2 -> {
                            ruta2Editar = saveImageToInternalStorage(bitmap)

                        }

                        3 -> {
                            ruta3Editar = saveImageToInternalStorage(bitmap)

                        }
                    }



                    imageAdapter.addImage(bitmap, posicion)
                } else {
                    Toast.makeText(
                        requireContext(), "Esta imagen ya ha sido seleccionada", Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(), "No se puede cargar la imagen", Toast.LENGTH_LONG
                ).show()
            }

        } else {
            Toast.makeText(
                requireContext(), "Ya no se puede seleccionar más fotos", Toast.LENGTH_LONG
            ).show()
        }
    }


    private fun saveImageToInternalStorage(bitmap: Bitmap): String? {
        val wrapper = ContextWrapper(requireContext().applicationContext)
        var file: File? = null
        try {

            val dir = wrapper.getDir("imagen", Context.MODE_PRIVATE)
            file = File(dir, "${System.currentTimeMillis()}.jpg")
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file?.absolutePath
    }

    private fun getBitmapFromUri(fromUri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(fromUri)
            if (inputStream != null) {
                BitmapFactory.decodeStream(inputStream)
            } else {
                Log.e("Error", "InputStream es nulo para la URI: $fromUri")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Error", "Error al obtener bitmap desde URI: ${e.message}")
            null
        }
    }


}




