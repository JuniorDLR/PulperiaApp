package com.example.pulperiaapp.ui.view.inventario.view


import android.annotation.SuppressLint
import android.app.Activity.*
import android.app.AlertDialog
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
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.example.pulperiaapp.R
import com.example.pulperiaapp.data.database.entitie.InventarioEntity
import com.example.pulperiaapp.databinding.FragmentEditarInventarioBinding
import com.example.pulperiaapp.domain.inventario.InventarioModel
import com.example.pulperiaapp.ui.view.inventario.adapter.ImageAdapter
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
    private val MAX = 3
    private var imageToken = 0
    private val bitmapList = mutableListOf<Bitmap>()
    private var esNuevo: Boolean = false


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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_editarInventarioFragment_to_inventarioDatosFragment)
        }

        binding.swMasProdcutos.setOnCheckedChangeListener { _, idCheked ->
            if (idCheked) {
                binding.btnTablaEditar.text = "Agregar datos"
                esNuevo = true
                limpiarTexto()

            } else {
                binding.btnTablaEditar.text = "Editar tabla"
                esNuevo = false


            }
        }


    }

    private fun limpiarTexto() {
        binding.tvNombreProductoEditar.setText("")
        binding.tvTamanoEnvaseEditar.setText("")
        binding.tvCantidadCajillasEditar.setText("")
        binding.tvCantidadPorCajillaEditar.setText("")
        binding.tvPrecioUnitarioEditar.setText("")
    }

    private fun guardarInventarioEditado() {
        val idInventario = args.idInventario

        inventarioRecuperado[idInventario]?.mapIndexed { index, lista ->
            val id = lista.id
            val nombre = lista.nombreProducto
            val tamano = lista.tamano
            val fecha = lista.fechaEntrega
            val fechaEditada =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val cantidadCajilla = lista.cantidadCajilla
            val cantidad = lista.cantidad
            val importe = lista.importe
            val idFoto = lista.idFotos


            val entity = InventarioEntity(
                id,
                nombre,
                tamano,
                fecha,
                fechaEditada,
                cantidadCajilla,
                cantidad,
                importe, idFoto
            )

            if (cantidad > 0) {
                lifecycleScope.launch { insertarOEditar(entity) }
            } else {
                inventarioModel.eliminarInventario(id)
            }

        }


        val action =
            EditarInventarioFragmentDirections.actionEditarInventarioFragmentToInventarioDatosFragment()
        Navigation.findNavController(binding.root).navigate(action)
        Toast.makeText(requireContext(), "Datos editados exitosamente", Toast.LENGTH_SHORT).show()
    }

    private suspend fun insertarOEditar(entity: InventarioEntity) {
        if (entity.id == 0) {
            inventarioModel.insertarInventario(entity)
        } else {
            inventarioModel.editarInventario(entity)
        }

    }

    private fun editarTabla() {

        val idInventario = args.idInventario
        val nombre = binding.tvNombreProductoEditar.text.toString()
        val tamano = binding.tvTamanoEnvaseEditar.text.toString()
        val cantidad = binding.tvCantidadCajillasEditar.text.toString()
        val cantidadCajilla = binding.tvCantidadPorCajillaEditar.text.toString()
        val import = binding.tvPrecioUnitarioEditar.text.toString()
        val fechaEntrega = args.idInventario


        lifecycleScope.launch {
            if (esNuevo) {

                val cantidadCajillaInt = cantidadCajilla.toInt()
                val cantidadInt = cantidad.toInt()
                val importDouble = import.toDouble()
                val nuevaLista = mutableListOf(
                    InventarioModel(
                        0,
                        nombre,
                        tamano,
                        fechaEntrega,
                        null,
                        cantidadCajillaInt,
                        cantidadInt,
                        importDouble, null
                    )
                )


                inventarioRecuperado[idInventario] = nuevaLista

                actualizarTabla()

            } else {

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
                    inventarioRecuperado[idInventario]?.find { it.id == productoSeleccionado }
                        ?.apply {
                            this.importe = import.toDouble()
                            this.nombreProducto = nombre
                            this.tamano = tamano
                            this.cantidad = cantidad.toInt()
                            this.cantidadCajilla = cantidadCajilla.toInt()
                            actualizarTabla()
                        }
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

            override fun onImageDelete(imageCount: Int, deletedImage: Bitmap?) {
                actualizarConteo(imageCount)
            }
        })

        viewPager = binding.vpFotoEditar
        viewPager.adapter = imageAdapter
    }


    private fun actualizarConteo(imageCount: Int) {
        Log.d("ConteoImagenes", "Valor de imageCount: $imageCount")
        val count = "$imageCount/$MAX"
        binding.contadorImage.text = count

        binding.btnTomarFotoEditar.visibility = if (imageCount < MAX) View.VISIBLE else View.GONE
        imageToken = imageCount
    }


    private fun initData(idInventario: String) {
        lifecycleScope.launch {
            inventarioModel.obtenerDetalleInventario(idInventario)
            inventarioModel.groupInventario.observe(viewLifecycleOwner) { lista ->
                lista?.let {
                    it.forEach { j ->
                        guardarDatosRecuperados(j, idInventario)
                    }

                    actualizarTabla()
                }
            }
        }
    }

    private fun guardarDatosRecuperados(inventario: InventarioModel, idFecha: String) {
        if (inventario.cantidad > 0) {
            inventarioRecuperado.getOrPut(idFecha) { mutableListOf() }.add(inventario)
        }


    }


    private fun actualizarTabla() {

        tableLayout.removeAllViews()
        val idCliente = args.idInventario
        var total = 0.0
        bitmapList.clear()
        inventarioRecuperado[idCliente]?.filter { it.cantidad > 0 }?.forEach { inventario ->
            total += inventario.importe

            agregarFila(inventario)
        }

        binding.tvPrecioPagar.text = total.toString()


    }

    @SuppressLint("InflateParams")
    private fun agregarFila(inventario: InventarioModel) {
        val table = LayoutInflater.from(requireContext())
            .inflate(R.layout.table_row_inventario, null) as TableRow


        val nombreView = table.findViewById<TextView>(R.id.tvProductoRow)
        nombreView.text = inventario.nombreProducto

        val tamanoView = table.findViewById<TextView>(R.id.tvTamanoR)
        tamanoView.text = inventario.tamano

        val cantidadView = table.findViewById<TextView>(R.id.tvCantidadR)
        cantidadView.text = inventario.cantidad.toString()

        val cantidadCajillaView = table.findViewById<TextView>(R.id.tvCajillaR)
        cantidadCajillaView.text = inventario.cantidadCajilla.toString()


        obtenerFoto(inventario)

        nombreView.setOnClickListener {
            productoSeleccionado = inventario.id
            ShowAlertDialog(inventario)
        }

        tableLayout.addView(table)
    }

    fun ShowAlertDialog(inventario: InventarioModel) {
        val opciones = arrayOf("Editar", "Eliminar")
        val alert = AlertDialog.Builder(requireContext())
            .setTitle("Elige una opcion")
            .setItems(opciones) { _, switch ->
                when (switch) {
                    0 -> editarProducto(inventario)
                    1 -> eliminarProducto()
                }
            }
        alert.show()
    }

    private fun editarProducto(inventario: InventarioModel) {

        binding.tvNombreProductoEditar.setText(inventario.nombreProducto)
        binding.tvTamanoEnvaseEditar.setText(inventario.tamano)
        binding.tvCantidadCajillasEditar.setText(inventario.cantidad.toString())
        binding.tvCantidadPorCajillaEditar.setText(inventario.cantidadCajilla.toString())
        binding.tvPrecioUnitarioEditar.setText(inventario.importe.toString())
    }

    private fun eliminarProducto() {

        inventarioRecuperado[args.idInventario]?.find { it.id == productoSeleccionado }?.apply {
            this.cantidad = 0
        }

        actualizarTabla()
    }

    private fun obtenerFoto(inventario: InventarioModel) {
        lifecycleScope.launch {
            val lista = inventarioModel.obtenerFoto()


            if (bitmapList.size < MAX) {
                var alcanzadoMaximo = false

                lista.forEach { objeto ->
                    if (objeto.idFotos == inventario.idFotos && !alcanzadoMaximo) {

                        if (!objeto.ruta1.isNullOrEmpty()) {
                            val ruta1 = BitmapFactory.decodeFile(objeto.ruta1)
                            if (ruta1 != null && !bitmapList.contains(ruta1)) {
                                bitmapList.add(ruta1)
                            }
                        }

                        if (!objeto.ruta2.isNullOrEmpty()) {
                            val ruta2 = BitmapFactory.decodeFile(objeto.ruta2)
                            if (ruta2 != null && !bitmapList.contains(ruta2)) {
                                bitmapList.add(ruta2)
                            }
                        }

                        if (!objeto.ruta3.isNullOrEmpty()) {
                            val ruta3 = BitmapFactory.decodeFile(objeto.ruta3)
                            if (ruta3 != null && !bitmapList.contains(ruta3)) {
                                bitmapList.add(ruta3)
                            }
                        }


                        alcanzadoMaximo = bitmapList.size >= MAX
                    }
                }

                imageAdapter.notifyDataSetChanged()
                binding.btnTomarFotoEditar.visibility =
                    if (imageAdapter.count == MAX) View.GONE else View.VISIBLE
            }
        }
    }


    private fun openGallery() {
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
                if (!bitmapUri) {
                    Log.d("EditarInventario", "ImageToken $imageToken")
                    val ruta = saveImageToInternalStorage(bitmap)
                    if (ruta != null) {
                        imageAdapter.addImage(bitmap)
                    } else {
                        Toast.makeText(
                            requireContext(), "Error al guardar la imagen", Toast.LENGTH_LONG
                        ).show()
                    }
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




