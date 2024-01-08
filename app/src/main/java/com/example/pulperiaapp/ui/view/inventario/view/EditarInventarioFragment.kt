package com.example.pulperiaapp.ui.view.inventario.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
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
    private lateinit var viewPager: ViewPager
    private val inventarioRecuperado = mutableMapOf<String, MutableList<InventarioModel>>()
    private lateinit var imageAdapter: ImageAdapter
    private val MAX = 3
    private var imageToken = 0
    private val bitmapList = mutableListOf<Bitmap>()
    private var esNuevo: Boolean = false
    private var productoSeleccionado: Int = 0

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.clipData?.let { clipData ->
                    (0 until clipData.itemCount).forEach { i ->
                        handleImageUri(clipData.getItemAt(i).uri)
                    }
                } ?: result.data?.data?.let { uri ->
                    handleImageUri(uri)
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
        initUI()
        setupListeners()
        initData(args.idInventario)
    }

    private fun initData(idInventario: String) {
        lifecycleScope.launch {
            inventarioModel.obtenerDetalleInventario(idInventario)
            inventarioModel.groupInventario.observe(viewLifecycleOwner) { lista ->
                lista?.let {
                    it.forEach { j ->
                        guardarDatosRecuperados(j, idInventario)
                    }

                    updateTable()
                }
            }
        }
    }

    private fun guardarDatosRecuperados(inventario: InventarioModel, idFecha: String) {
        if (inventario.cantidad > 0) {
            inventarioRecuperado.getOrPut(idFecha) { mutableListOf() }.add(inventario)
        }


    }

    private fun initUI() {
        tableLayout = binding.tlProductoInventario
        imageAdapter = ImageAdapter(bitmapList, object : ImageCounterListener {
            override fun onImageAdd(imageCount: Int) = updateImageCount(imageCount)
            override fun onImageDelete(imageCount: Int, deletedImage: Bitmap?) =
                updateImageCount(imageCount)
        })
        viewPager = binding.vpFotoEditar
        viewPager.adapter = imageAdapter
    }

    private fun setupListeners() {
        binding.btnTablaEditar.setOnClickListener { editarTabla() }
        binding.btnTomarFotoEditar.setOnClickListener { openGallery() }
        binding.btnGuardar.setOnClickListener { guardarInventarioEditado() }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_editarInventarioFragment_to_inventarioDatosFragment)
        }

        binding.swMasProdcutos.setOnCheckedChangeListener { _, isChecked ->
            binding.btnTablaEditar.text = if (isChecked) "Agregar datos" else "Editar tabla"
            esNuevo = isChecked
            if (isChecked) clearTextFields()
        }
    }

    private fun clearTextFields() {
        with(binding) {
            tvNombreProductoEditar.setText("")
            tvTamanoEnvaseEditar.setText("")
            tvCantidadCajillasEditar.setText("")
            tvCantidadPorCajillaEditar.setText("")
            tvPrecioUnitarioEditar.setText("")
        }
    }

    private fun guardarInventarioEditado() {
        val idInventario = args.idInventario

        inventarioRecuperado[idInventario]?.forEach { lista ->
            val entity = lista.toInventarioEntity()
            if (entity.cantidad > 0) {
                lifecycleScope.launch { insertOrUpdateInventory(entity) }
            } else {
                inventarioModel.eliminarInventario(entity.id)
            }
        }

        val action =
            EditarInventarioFragmentDirections.actionEditarInventarioFragmentToInventarioDatosFragment()
        Navigation.findNavController(binding.root).navigate(action)
        showToast("Datos editados exitosamente")
    }

    private fun InventarioModel.toInventarioEntity(): InventarioEntity {
        val fechaEditada =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        return InventarioEntity(
            id,
            nombreProducto,
            tamano,
            fechaEntrega,
            fechaEditada,
            cantidadCajilla,
            cantidad,
            importe, idFotos
        )
    }

    private suspend fun insertOrUpdateInventory(entity: InventarioEntity) {
        if (entity.id == 0) {
            inventarioModel.insertarInventario(entity)
        } else {
            inventarioModel.editarInventario(entity)
        }
    }

    private fun editarTabla() {
        val idInventario = args.idInventario

        lifecycleScope.launch {
            if (esNuevo) {
                val nuevaLista = mutableListOf(
                    InventarioModel(
                        0,
                        binding.tvNombreProductoEditar.text.toString(),
                        binding.tvTamanoEnvaseEditar.text.toString(),
                        idInventario,
                        null,
                        binding.tvCantidadPorCajillaEditar.text.toString().toInt(),
                        binding.tvCantidadCajillasEditar.text.toString().toInt(),
                        binding.tvPrecioUnitarioEditar.text.toString().toDouble(),
                        null
                    )
                )
                inventarioRecuperado[idInventario] = nuevaLista
                updateTable()
            } else {
                val emptyFields = mutableListOf<TextInputEditText>()

                with(binding) {
                    if (tvNombreProductoEditar.text?.isEmpty() == true) emptyFields.add(
                        tvNombreProductoEditar
                    )
                    if (tvTamanoEnvaseEditar.text?.isEmpty() == true) emptyFields.add(
                        tvTamanoEnvaseEditar
                    )
                    if (tvCantidadPorCajillaEditar.text?.isEmpty() == true) emptyFields.add(
                        tvCantidadPorCajillaEditar
                    )
                    if (tvCantidadCajillasEditar.text?.isEmpty() == true) emptyFields.add(
                        tvCantidadCajillasEditar
                    )
                    if (tvPrecioUnitarioEditar.text?.isEmpty() == true) emptyFields.add(
                        tvPrecioUnitarioEditar
                    )
                }

                if (emptyFields.isNotEmpty()) {
                    emptyFields[0].requestFocus()
                } else {
                    inventarioRecuperado[idInventario]?.find { it.id == productoSeleccionado }
                        ?.apply {
                            with(binding) {
                                importe = tvPrecioUnitarioEditar.text.toString().toDouble()
                                nombreProducto = tvNombreProductoEditar.text.toString()
                                tamano = tvTamanoEnvaseEditar.text.toString()
                                cantidad = tvCantidadCajillasEditar.text.toString().toInt()
                                cantidadCajilla = tvCantidadPorCajillaEditar.text.toString().toInt()
                            }
                            updateTable()
                        }
                }
            }
        }
    }

    private fun updateTable() {
        tableLayout.removeAllViews()
        val idCliente = args.idInventario
        var total = 0.0
        bitmapList.clear()

        inventarioRecuperado[idCliente]?.filter { it.cantidad > 0 }?.forEach { inventario ->
            total += inventario.importe
            addRowToTable(inventario)
        }

        binding.tvPrecioPagar.text = "Precio Total: $total"
        updateImageCount(imageAdapter.count)
    }

    private fun addRowToTable(inventario: InventarioModel) {
        val tableRow = LayoutInflater.from(requireContext())
            .inflate(R.layout.table_row_inventario, null) as TableRow

        with(tableRow) {
            findViewById<TextView>(R.id.tvProductoRow).text = inventario.nombreProducto
            findViewById<TextView>(R.id.tvTamanoR).text = inventario.tamano
            findViewById<TextView>(R.id.tvCantidadR).text = inventario.cantidad.toString()
            findViewById<TextView>(R.id.tvCajillaR).text = inventario.cantidadCajilla.toString()
        }

        obtenerFoto(inventario)

        tableRow.setOnClickListener {
            productoSeleccionado = inventario.id
            showAlertDialog(inventario)
        }

        tableLayout.addView(tableRow)
    }

    private fun showAlertDialog(inventario: InventarioModel) {
        val opciones = arrayOf("Editar", "Eliminar")
        AlertDialog.Builder(requireContext())
            .setTitle("Elige una opcion")
            .setItems(opciones) { _, switch ->
                when (switch) {
                    0 -> editarProducto(inventario)
                    1 -> eliminarProducto()
                }
            }
            .show()
    }

    private fun editarProducto(inventario: InventarioModel) {
        with(binding) {
            tvNombreProductoEditar.setText(inventario.nombreProducto)
            tvTamanoEnvaseEditar.setText(inventario.tamano)
            tvCantidadCajillasEditar.setText(inventario.cantidad.toString())
            tvCantidadPorCajillaEditar.setText(inventario.cantidadCajilla.toString())
            tvPrecioUnitarioEditar.setText(inventario.importe.toString())
        }
    }

    private fun eliminarProducto() {
        inventarioRecuperado[args.idInventario]?.find { it.id == productoSeleccionado }?.apply {
            cantidad = 0
        }
        updateTable()
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
                updateImageCount(imageAdapter.count)
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
                    val ruta = saveImageToInternalStorage(bitmap)
                    if (ruta != null) {
                        imageAdapter.addImage(bitmap)
                    } else {
                        showToast("Error al guardar la imagen")
                    }
                } else {
                    showToast("Esta imagen ya ha sido seleccionada")
                }
            } else {
                showToast("No se puede cargar la imagen")
            }
        } else {
            showToast("Ya no se puede seleccionar más fotos")
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
            inputStream?.let { BitmapFactory.decodeStream(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun updateImageCount(imageCount: Int) {
        val count = "$imageCount/$MAX"
        binding.contadorImage.text = count
        binding.btnTomarFotoEditar.visibility = if (imageCount < MAX) View.VISIBLE else View.GONE
        imageToken = imageCount
    }
}
