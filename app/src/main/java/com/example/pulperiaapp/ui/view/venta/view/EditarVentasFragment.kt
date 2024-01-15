package com.example.pulperiaapp.ui.view.venta.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.pulperiaapp.R
import com.example.pulperiaapp.data.database.entitie.VentaPrixCoca
import com.example.pulperiaapp.databinding.FragmentEditarVentasBinding
import com.example.pulperiaapp.domain.venta.DetalleEditar
import com.example.pulperiaapp.ui.view.venta.viewmodel.VentaViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class EditarVentasFragment : Fragment() {
    private lateinit var binding: FragmentEditarVentasBinding
    private lateinit var tableLayou: TableLayout
    private lateinit var tableRow: TableRow
    private val ventaModel: VentaViewModel by viewModels()
    private lateinit var chipGroup: ChipGroup
    private val args: EditarVentasFragmentArgs by navArgs()


    private val productoEditar = mutableMapOf<Int, MutableList<DetalleEditar>>()
    val respaldo = LinkedHashMap<Int, MutableList<DetalleEditar>>()
    private var esRespaldo: Boolean = false
    private var listener: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentEditarVentasBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val idEditar: Int = args.idProducto

        iniComponent()
        initData(idEditar)

        val isMultiple = args.isMultiple



        if (isMultiple) {
            binding.toolbarVentas.title = "Modo espectador - Ventas"
            binding.ChipGroup.isVisible = false
            binding.seleccionPro.isVisible = false
            binding.btnEditarVenta.isVisible = false
            binding.swVentaPorCajillaEditar.isVisible = false
            binding.tvPrecioPersonalizado.isVisible = false
            binding.btnRestablecer.isVisible = false
            binding.inputPrecio.isVisible = false
        }

        binding.btnRestablecer.setOnClickListener {
            listener = true
            lifecycleScope.launch { obtenerPrecio() }

        }


        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->

            for (idList in checkedIds) {
                val selected = group.findViewById(idList) as Chip
                when (selected.tag) {
                    "Prixcola" -> ventaPrix()
                    "BigCola" -> ventaBigCola()
                    "Coca" -> ventaCoca()
                    else -> {
                        Toast.makeText(requireContext(), "Seleccion no valida", Toast.LENGTH_LONG)
                            .show()
                    }

                }
            }
        }




        binding.btnEditarVenta.setOnClickListener {
            guardarVentaEditada()
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

            if (isMultiple) {

                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_editarVentasFragment_to_filtrarDatosFragment)

            } else {

                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_editarVentasFragment_to_ventasFragment)
            }

        }


        binding.tvPrecioPersonalizado.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                respaldo.clear()
                respaldo.putAll(productoEditar)
            }


            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(texto: Editable?) {
                val precioPersonalizado = texto.toString().toDoubleOrNull()
                actualizarEnTiempoReal(precioPersonalizado)
            }


        })

        binding.swVentaPorCajillaEditar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Error")
                .setMessage("No puedes cambiar el estado  de ventas por cajilla, solo puedes agregar o editar")
                .setPositiveButton("OK", null)
                .show()


            binding.swVentaPorCajillaEditar.isChecked = true

        }


    }


    private suspend fun obtenerPrecio() {
        val productoCompleto = ventaModel.obtenerTodosLosProductos()


        productoEditar.forEach { (_, detalles) ->
            detalles.forEach { detalle ->
                val tipoProducto = determinarTipoProducto(detalle.producto, productoCompleto)
                val precioOriginal =
                    obtenerPrecioSegunSuTipo(tipoProducto, detalle.producto, productoCompleto)
                detalle.totalVenta = precioOriginal

                if (precioOriginal == 0.0) {


                    showAlertDialog(
                        "ERROR",
                        "Un producto no tiene un precio registrado. Puede haber sido eliminado."
                    )
                }
            }
        }

        restablecer()
    }


    private fun determinarTipoProducto(
        nombreProducto: String,
        productoCompleto: List<String>
    ): String {

        val productoEncontrado =
            productoCompleto.find { it.equals(nombreProducto, ignoreCase = true) }
        return productoEncontrado ?: ""

    }


    private suspend fun obtenerPrecioSegunSuTipo(
        tipoProducto: String,
        producto: String,
        productoCompleto: List<String>
    ): Double {
        return when {
            productoCompleto.contains(tipoProducto) -> ventaModel.obtenerPrecio(producto)
            else -> 0.0
        }

    }

    private fun restablecer() {
        productoEditar.forEach { (_, detalles) ->
            detalles.forEach { detalle ->
                val precioNuevo = detalle.cantidad * detalle.totalVenta
                detalle.totalVenta = precioNuevo
            }
        }
        visualizarTabla(false)
    }

    private fun actualizarEnTiempoReal(precioPersonalizado: Double?) {
        val idEditar = args.idProducto


        if (precioPersonalizado != null && binding.swVentaPorCajillaEditar.isChecked) {
            esRespaldo = true
            listener = false

            val cantidadTotal = respaldo.values.sumOf { lista ->
                lista.sumOf { it.cantidad }
            }

            val precioPorUnidad = precioPersonalizado / cantidadTotal

            respaldo[idEditar]?.forEach { lista ->
                val cantidad = lista.cantidad
                val nuevoPrecio = cantidad * precioPorUnidad
                lista.totalVenta = nuevoPrecio
            }


            visualizarTabla(true)
        } else {
            respaldo.clear()
            esRespaldo = false
            visualizarTabla(false)
        }
    }


    private fun guardarVentaEditada() {
        val ventaIndividual = args.esIndividual
        if (listener) {
            Snackbar.make(
                requireView(),
                "Los precios son los mismos, debes personalizarlo",
                Snackbar.LENGTH_LONG
            ).show()

        } else if (ventaIndividual) {
            ventaEditada()
        } else {
            ventaEditada()
        }

    }

    private fun ventaEditada() {
        productoEditar.forEach { (_, detalles) ->
            detalles.forEach { detalle ->
                val id = detalle.id
                val producto = detalle.producto
                val cantidad = detalle.cantidad
                val precio = detalle.totalVenta
                val esVentaPorCajilla = binding.swVentaPorCajillaEditar.isChecked

                val fecha = args.idFecha
                val fechaFormateada =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                // Edición, usar ID existente
                val venta = VentaPrixCoca(
                    id = id,
                    producto = producto,
                    total = precio,
                    fecha = fecha,
                    fechaEditada = fechaFormateada,
                    ventaPorCajilla = esVentaPorCajilla,
                    cantidad = cantidad
                )

                if (cantidad > 0) {
                    insertarOVenta(venta)
                } else {
                    ventaModel.eliminarVenta(id)

                }

            }

        }

        Toast.makeText(requireContext(), "Datos editados exitosamente", Toast.LENGTH_LONG)
            .show()
        val action =
            EditarVentasFragmentDirections.actionEditarVentasFragmentToVentasFragment()
        Navigation.findNavController(binding.root).navigate(action)
    }

    private fun insertarOVenta(venta: VentaPrixCoca) {
        lifecycleScope.launch {
            if (venta.id == 0) {
                ventaModel.insertarVenta(venta)
            } else {
                ventaModel.editarVenta(venta)
            }
        }
    }


    private fun iniComponent() {
        tableLayou = binding.tlProductoEditar
        tableRow = binding.trProductoEditar
        chipGroup = binding.chListaEditar
    }

    private fun initData(idEditar: Int) {
        val fechaActual: String = args.idFecha
        lifecycleScope.launch {
            ventaModel.obtenerDetalleEditarLiveData(fechaActual)
            ventaModel.data.collect { detalleList ->
                detalleList.let {
                    it.forEach { j ->
                        if ((j.id == idEditar && !j.ventaPorCajilla) || (j.ventaPorCajilla)) {
                            guardarDatoRecuperado(idEditar, j)
                        }
                    }

                    visualizarTabla(false)
                }
            }
        }
    }


    private fun guardarDatoRecuperado(idEditar: Int, detalleList: DetalleEditar) {
        val esIndividual = args.esIndividual
        val existingList = productoEditar[idEditar]?.toMutableList() ?: mutableListOf()

        if (esIndividual) {
            existingList.clear()
            binding.swVentaPorCajillaEditar.isEnabled = false
            binding.tvPrecioPersonalizado.isEnabled = false
            binding.inputPrecio.isEnabled = false
            binding.btnRestablecer.isEnabled = false

        }
        existingList.add(detalleList)
        productoEditar[idEditar] = existingList
    }


    private fun visualizarTabla(esRespaldo: Boolean) {
        tableLayou.removeAllViews()
        var totalView = 0.0
        val idEditar: Int = args.idProducto

        if (esRespaldo) {
            respaldo[idEditar]?.filter { it.cantidad > 0 }?.forEach { lista ->
                val idProdcuto = lista.id
                val producto = lista.producto
                val cantidad = lista.cantidad
                val total = lista.totalVenta
                val esCajilla = lista.ventaPorCajilla
                totalView += total
                if (esCajilla) binding.swVentaPorCajillaEditar.isChecked = true

                agregarFila(producto, cantidad, total, idEditar, idProdcuto)

            }
        } else {
            productoEditar[idEditar]?.filter { it.cantidad > 0 }?.forEach { lista ->
                val idProdcuto = lista.id
                val producto = lista.producto
                val cantidad = lista.cantidad
                val total = lista.totalVenta
                val esCajilla = lista.ventaPorCajilla
                totalView += total
                if (esCajilla) binding.swVentaPorCajillaEditar.isChecked = true

                agregarFila(producto, cantidad, total, idEditar, idProdcuto)
            }
        }
        val precioFormateado = formatearPrecio(totalView)
        binding.tvTotalAmountEditar.text = precioFormateado
    }

    @SuppressLint("InflateParams")
    private fun agregarFila(
        producto: String,
        cantidad: Int,
        total: Double,
        idEditar: Int,
        idProdcuto: Int
    ) {

        val tableRow = LayoutInflater.from(requireContext())
            .inflate(R.layout.table_row_venta, null) as TableRow
        tableRow.tag = idProdcuto


        val productoView = tableRow.findViewById<TextView>(R.id.tvProductoVenta)
        productoView.text = producto

        val cantidadView = tableRow.findViewById<TextView>(R.id.tvCantidadVenta)
        cantidadView.text = cantidad.toString()

        val precioFormateado = formatearPrecio(total)

        val precioView = tableRow.findViewById<TextView>(R.id.tvPrecioVenta)
        precioView.text = precioFormateado



        cantidadView.setOnClickListener {
            if (args.isMultiple) {
                showSnackbar("No puedes editar, estás en modo espectador")
            } else if (!binding.swVentaPorCajillaEditar.isChecked) {


                showQuantityDialog(cantidad, total, cantidadView, idEditar, idProdcuto)
            } else if (!listener) {
                showAlertDialog(
                    "ADVERTENCIA",
                    "Si quieres editar la cantidad o agregar un nuevo producto, debes restablecer el precio primero"
                )
            } else {
                showQuantityDialog(cantidad, total, cantidadView, idEditar, idProdcuto)
            }
        }

        tableLayou.addView(tableRow)
        binding.tvTotalAmountEditar.text = total.toString()

    }


    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    private fun showAlertDialog(title: String, message: String) {
        val alert = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Continuar") { dialog, _ ->
                dialog.dismiss()
            }

        alert.show()
    }
    private fun formatearPrecio(precio: Double): String? {
        val bigDecimal = BigDecimal.valueOf(precio)
        val format = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.getDefault()))
        return format.format(bigDecimal)
    }
    private fun showQuantityDialog(
        cantidad: Int,
        total: Double,
        cantidadView: TextView,
        idEditar: Int,
        idProdcuto: Int
    ) {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Modificar cantidad")
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.setText(cantidadView.text)

        builder.setView(input)

        builder.setPositiveButton("Aceptar") { dialog, _ ->
            val nuevaCantidad = input.text.toString().toInt()
            val nuevoPrecio = nuevaCantidad * total / cantidad

            val detalleExistente = productoEditar[idEditar]?.find { it.id == idProdcuto }
            detalleExistente?.let {
                it.cantidad = nuevaCantidad
                it.totalVenta = nuevoPrecio
            }
            visualizarTabla(false)

            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }


        builder.setNeutralButton("ELIMINAR") { dialog, _ ->
            val detalleExistente = productoEditar[idEditar]?.find { it.id == idProdcuto }
            detalleExistente?.let {
                it.cantidad = 0
                it.totalVenta = 0.0
            }
            visualizarTabla(false)
            dialog.dismiss()

        }

        builder.show()
    }


    private fun ventaCoca() {
        lifecycleScope.launch {
            val lista = ventaModel.obtenerProductoCoca()
            val spinner = binding.spProductosEditar
            val opcion = listOf("Lista Coca") + lista

            val adapter =
                ArrayAdapter(requireContext(), R.layout.simple_spinner_item, opcion)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter


            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    id: Long
                ) {
                    val productoSeleccionado = opcion[position]
                    binding.btnEditarProducto.setOnClickListener {
                        if (position == 0 && productoSeleccionado == "Lista Coca") {
                            Snackbar.make(
                                requireView(),
                                "Debes de seleccionar un producto",
                                Snackbar.LENGTH_LONG
                            ).show()
                        } else {

                            val esIndividual = args.esIndividual
                            if (esIndividual) {
                                seleccionSpinnerCoca(productoSeleccionado)
                            } else if (!listener) {
                                Snackbar.make(
                                    requireView(),
                                    "para agregar debes restablecer el precio ",
                                    Snackbar.LENGTH_LONG
                                ).show()

                            } else {
                                seleccionSpinnerCoca(productoSeleccionado)
                            }
                        }
                    }

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
        }
    }

    private fun ventaBigCola() {
        lifecycleScope.launch {
            val lista = ventaModel.obtenerProductoBig()
            val opcion = listOf("Lista Big") + lista
            val spinner = binding.spProductosEditar

            val adapter =
                ArrayAdapter(requireContext(), R.layout.simple_spinner_item, opcion)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter


            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    id: Long
                ) {
                    val productoSeleccionado = opcion[position]
                    binding.btnEditarProducto.setOnClickListener {
                        if (position == 0 && productoSeleccionado == "Lista Big") {
                            Snackbar.make(
                                requireView(),
                                "Debes de seleccionar un producto",
                                Snackbar.LENGTH_LONG
                            ).show()
                        } else {

                            val esIndividual = args.esIndividual
                            if (esIndividual) {
                                seleccionSpinnerBig(productoSeleccionado)

                            } else if (!listener) {

                                Snackbar.make(
                                    requireView(),
                                    "para agregar debes restablecer el precio ",
                                    Snackbar.LENGTH_LONG
                                ).show()


                            } else {
                                seleccionSpinnerBig(productoSeleccionado)
                            }

                        }
                    }

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
        }
    }




    private fun ventaPrix() {
        lifecycleScope.launch {
            val spinnerPrix = binding.spProductosEditar
            val listaString = ventaModel.obtenerProdcutoPrix()
            val opcion = listOf("Lista prix") + listaString

            val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, opcion)

            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinnerPrix.adapter = adapter


            spinnerPrix.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    id: Long
                ) {
                    val productoSeleccionado = opcion[position]
                    binding.btnEditarProducto.setOnClickListener {

                        if (position == 0 && productoSeleccionado == "Lista prix") {

                            Snackbar.make(
                                requireView(),
                                "Debes de seleccionar un producto ",
                                Snackbar.LENGTH_LONG
                            ).show()

                        } else {
                            val esIndividual = args.esIndividual
                            if (esIndividual) {
                                seleccionSpinnerPrix(productoSeleccionado)
                            } else if (!listener) {

                                Snackbar.make(
                                    requireView(),
                                    "para agregar debes restablecer el precio ",
                                    Snackbar.LENGTH_LONG
                                ).show()

                            } else {
                                seleccionSpinnerPrix(productoSeleccionado)
                            }

                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Log.d("Seleccion", "Ningún producto seleccionado")
                }

            }
        }

    }

    private fun seleccionSpinnerPrix(productoSeleccionado: String) {
        lifecycleScope.launch {
            val esCajilla = binding.swVentaPorCajillaEditar.isChecked
            val precioProducto = ventaModel.obtenerPrecioPrix(productoSeleccionado)
            if (precioProducto != null) {
                guardarVenta(
                    productoSeleccionado,
                    precioProducto,
                    esCajilla
                )
            }

        }
    }

    private fun seleccionSpinnerCoca(productoSeleccionado: String) {
        lifecycleScope.launch {
            val esCajilla = binding.swVentaPorCajillaEditar.isChecked
            val precioProducto = ventaModel.obtenerPrecioCoca(productoSeleccionado)
            if (precioProducto != null) {
                guardarVenta(
                    productoSeleccionado,
                    precioProducto,
                    esCajilla
                )
            }

        }
    }

    private fun seleccionSpinnerBig(productoSeleccionado: String) {
        lifecycleScope.launch {
            val esCajilla = binding.swVentaPorCajillaEditar.isChecked
            val precioProducto = ventaModel.obtenerPrecioBig(productoSeleccionado)
            if (precioProducto != null) {
                guardarVenta(
                    productoSeleccionado,
                    precioProducto,
                    esCajilla
                )
            }

        }
    }

    fun guardarVenta(productoSeleccionado: String, precioProducto: Double, esCajilla: Boolean) {
        val cliente = args.idProducto
        val cantidadInicial = 1

        if (binding.swVentaPorCajillaEditar.isChecked) {
            // Lógica cuando el switch está activado
            val listaDetalles = productoEditar[cliente]

            if (listaDetalles != null) {
                val detalleExistente = listaDetalles.find { it.producto == productoSeleccionado }
                if (detalleExistente != null) {
                    val nuevaCantidad = detalleExistente.cantidad + cantidadInicial
                    val nuevoPrecio = nuevaCantidad * precioProducto
                    detalleExistente.cantidad = nuevaCantidad
                    detalleExistente.totalVenta = nuevoPrecio
                } else {
                    val nuevoPrecio = cantidadInicial * precioProducto
                    val nuevoDetalle = DetalleEditar(
                        0,
                        productoSeleccionado,
                        cantidadInicial,
                        nuevoPrecio,
                        esCajilla
                    )
                    listaDetalles.add(nuevoDetalle)
                }
            } else {
                // Si no hay detalles para este cliente, crea una nueva lista con el nuevo detalle
                val nuevaLista = mutableListOf(
                    DetalleEditar(
                        0,
                        productoSeleccionado,
                        cantidadInicial,
                        precioProducto,
                        esCajilla
                    )
                )
                productoEditar[cliente] = nuevaLista
            }
            visualizarTabla(false)
        } else {
            // Lógica cuando el switch NO está activado
            val productoTriple = productoEditar[cliente]

            if (productoTriple != null) {
                val detalleExistente =
                    productoTriple.find { it.producto == productoSeleccionado }
                if (detalleExistente != null) {
                    val nuevaCantidad = detalleExistente.cantidad + cantidadInicial
                    val nuevoPrecio = nuevaCantidad * precioProducto
                    detalleExistente.cantidad = nuevaCantidad
                    detalleExistente.totalVenta = nuevoPrecio
                } else {
                    val detalle =
                        DetalleEditar(
                            cliente,
                            productoSeleccionado,
                            cantidadInicial,
                            precioProducto,
                            esCajilla
                        )
                    productoEditar[cliente] = listOf(detalle).toMutableList()
                }
            } else {
                val nuevaLista = mutableListOf(
                    DetalleEditar(
                        cliente,
                        productoSeleccionado,
                        cantidadInicial,
                        precioProducto,
                        esCajilla
                    )
                )
                productoEditar[cliente] = nuevaLista
            }
            visualizarTabla(false)
        }
    }

}