package com.example.pulperiaapp.ui.view.venta.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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


    }

    private suspend fun insertarOVenta(venta: VentaPrixCoca) {

        if (venta.id == 0) {
            // Es una nueva venta, insertarla
            ventaModel.insertarVenta(venta)
        } else if (venta.cantidad > 0) {
            // La cantidad es mayor que 0, editar la venta existente
            ventaModel.editarVenta(venta)
        }

    }


    private fun guardarVentaEditada() {
        val idEditar: Int = args.idProducto

        lifecycleScope.launch {
            if (productoEditar.isEmpty()) {
                Snackbar.make(
                    requireView(),
                    "No hay datos en la tabla",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                productoEditar[idEditar]?.forEach { info ->
                    val id = info.id
                    val producto = info.producto
                    val cantidad = info.cantidad
                    val precio = info.totalVenta
                    val esVentaPorCajilla = binding.swVentaPorCajillaEditar.isChecked

                    // Obtener la fecha actual dentro del bucle para cada venta
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
                        Log.d("Eliminacion", "${venta.id}")
                        ventaModel.eliminarVenta(venta.id)
                    }

                }
                Toast.makeText(requireContext(), "Datos editados exitosamente", Toast.LENGTH_LONG)
                    .show()
                requireActivity().supportFragmentManager.popBackStack()
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
            ventaModel.data.observe(viewLifecycleOwner) { detalleList ->
                detalleList?.let {
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
            existingList.clear() // Si es individual, borramos la lista existente
        }
        existingList.add(detalleList)
        productoEditar[idEditar] = existingList
    }


    private fun visualizarTabla(isChecked: Boolean) {
        tableLayou.removeAllViews()
        var totalView = 0.0
        val idEditar: Int = args.idProducto
        if (!isChecked && productoEditar.isNotEmpty()) {

            // Agregar elementos de productoEditar
            productoEditar[idEditar]?.filter { it.cantidad > 0 }?.forEach { lista ->
                val idProdcuto = lista.id
                val producto = lista.producto
                val cantidad = lista.cantidad
                val total = lista.totalVenta
                val esCajilla = lista.ventaPorCajilla
                totalView += total
                if (esCajilla) binding.swVentaPorCajillaEditar.isChecked = true

                agregarFila(producto, cantidad, total, idEditar, esCajilla, idProdcuto)

            }
        } else if (isChecked && productoEditar.isNotEmpty()) {

            // Agregar elementos de productoAgregado
            productoEditar[idEditar]?.filter { it.cantidad > 0 }?.forEach { info ->
                val idAgregado = info.id
                val producto = info.producto
                val cantidad = info.cantidad
                val total = info.totalVenta
                val esCajilla = info.ventaPorCajilla
                totalView += total
                agregarFila(producto, cantidad, total, -1, esCajilla, idAgregado)
            }
        }

        binding.tvTotalAmountEditar.text = totalView.toString()
    }

    @SuppressLint("InflateParams")
    private fun agregarFila(
        producto: String,
        cantidad: Int,
        total: Double,
        idEditar: Int,
        esCajilla: Boolean,
        idProdcuto: Int
    ) {

        val tableRow = LayoutInflater.from(requireContext())
            .inflate(R.layout.table_row_venta, null) as TableRow
        tableRow.tag = idProdcuto


        val productoView = tableRow.findViewById<TextView>(R.id.tvProductoVenta)
        productoView.text = producto

        val cantidadView = tableRow.findViewById<TextView>(R.id.tvCantidadVenta)
        cantidadView.text = cantidad.toString()

        val precioView = tableRow.findViewById<TextView>(R.id.tvPrecioVenta)
        precioView.text = total.toString()

        productoView.setOnClickListener {


            if (args.isMultiple) {
                Snackbar.make(
                    requireView(),
                    "No puedes editar, estas en modo espectador",
                    Snackbar.LENGTH_LONG
                ).show()

            } else {
                if (cantidad >= 1) {
                    val nuevaCantidad = cantidad - 1
                    val newPrecio = nuevaCantidad * total / cantidad
                    if (idEditar != -1) {
                        val detalleExistent = productoEditar[idEditar]?.find { it.id == idProdcuto }
                        detalleExistent?.let {
                            it.cantidad = nuevaCantidad
                            it.totalVenta = newPrecio
                        }

                        visualizarTabla(false)
                    } else {
                        // Si es de productoAgregado
                        val lista = mutableListOf(
                            DetalleEditar(
                                idProdcuto,
                                producto,
                                cantidad,
                                total,
                                esCajilla
                            )
                        )
                        productoEditar[idEditar] = lista
                        visualizarTabla(false)
                    }

                } else {
                    if (idEditar != -1) {
                        val detalleExistente =
                            productoEditar[idEditar]?.find { it.id == idProdcuto }
                        detalleExistente?.let {
                            productoEditar[idEditar]?.remove(it)

                        }

                        visualizarTabla(false)
                    } else {
                        val detalleExistente =
                            productoEditar[idEditar]?.find { it.id == idProdcuto }
                        detalleExistente?.let {
                            productoEditar[idEditar]?.remove(it)
                        }
                    }
                    binding.tvTotalAmountEditar.text = 0.0.toString()
                    visualizarTabla(false)
                }
            }
        }

        cantidadView.setOnClickListener {
            if (args.isMultiple) {
                Snackbar.make(
                    requireView(),
                    "No puedes editar, estas en modo espectador",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                if (cantidad >= 1) {
                    val nuevaCantidad = cantidad + 1
                    val newPrecio = nuevaCantidad * total / cantidad
                    if (idEditar != -1) {
                        val detalleExistent = productoEditar[idEditar]?.find { it.id == idProdcuto }
                        detalleExistent?.let {
                            it.cantidad = nuevaCantidad
                            it.totalVenta = newPrecio
                        }
                        visualizarTabla(false)
                    } else {
                        val lista = mutableListOf(
                            DetalleEditar(
                                idProdcuto,
                                producto,
                                cantidad,
                                total,
                                esCajilla
                            )
                        )
                        productoEditar[idEditar] = lista
                    }
                    visualizarTabla(false)
                } else {

                    if (idEditar != -1) {
                        val detalleExistente =
                            productoEditar[idEditar]?.find { it.id == idProdcuto }
                        detalleExistente?.let {
                            productoEditar[idEditar]?.remove(it)
                        }
                        visualizarTabla(false)
                    } else {
                        val detalleExistente =
                            productoEditar[idEditar]?.find { it.id == idProdcuto }
                        detalleExistente?.let {
                            productoEditar[idEditar]?.remove(it)
                        }
                    }
                    binding.tvTotalAmountEditar.text = 0.0.toString()
                    visualizarTabla(false)
                }
            }

        }

        tableLayou.addView(tableRow)
        tableLayou.requestLayout()
        binding.tvTotalAmountEditar.text = total.toString()
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
                            lifecycleScope.launch {
                                val esCajilla = binding.swVentaPorCajillaEditar.isChecked
                                val precioProducto =
                                    ventaModel.obtenerPrecioCoca(productoSeleccionado)
                                guardarVenta(productoSeleccionado, precioProducto, esCajilla)
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
                            lifecycleScope.launch {
                                val esCajilla = binding.swVentaPorCajillaEditar.isChecked
                                val precio = ventaModel.obtenerPrecioBig(productoSeleccionado)
                                guardarVenta(productoSeleccionado, precio, esCajilla)
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
                                "Debes de seleccionar un producto",
                                Snackbar.LENGTH_LONG
                            ).show()

                        } else {
                            lifecycleScope.launch {
                                val esCajilla = binding.swVentaPorCajillaEditar.isChecked
                                val precioProducto =
                                    ventaModel.obtenerPrecioPrix(productoSeleccionado)
                                guardarVenta(productoSeleccionado, precioProducto, esCajilla)

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


    fun guardarVenta(productoSeleccionado: String, precioProducto: Double, esCajilla: Boolean) {
        val cliente = args.idProducto
        val cantidadInicial = 1

        if (binding.swVentaPorCajillaEditar.isChecked) {
            // Lógica cuando el switch está activado
            val listaDetalles = productoEditar[cliente]

            if (listaDetalles != null) {
                val detalleExistente =
                    listaDetalles.find { it.producto == productoSeleccionado }
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
            visualizarTabla(true)
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