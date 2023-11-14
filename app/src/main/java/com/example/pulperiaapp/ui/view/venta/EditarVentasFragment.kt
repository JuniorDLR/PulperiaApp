package com.example.pulperiaapp.ui.view.venta

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.pulperiaapp.R
import com.example.pulperiaapp.data.database.entitie.VentaPrixCoca
import com.example.pulperiaapp.databinding.FragmentEditarVentasBinding
import com.example.pulperiaapp.ui.view.venta.viewmodel.VentaViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EditarVentasFragment : Fragment() {
    private lateinit var binding: FragmentEditarVentasBinding
    private lateinit var tableLayou: TableLayout
    private lateinit var tableRow: TableRow
    private val ventaModel: VentaViewModel by viewModels()
    private lateinit var chipGroup: ChipGroup
    private val args: EditarVentasFragmentArgs by navArgs()


    private val productoEditar = mutableMapOf<Int, Triple<String, Int, Double>>()
    private val productoAgregado = mutableMapOf<String, Pair<Int, Double>>()
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

        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->

            for (idList in checkedIds) {
                val selected = group.findViewById(idList) as Chip
                when (selected.tag) {
                    "Prixcola" -> ventaPrix()
                    "BigCola" -> ventaBigCola()
                    "Coca" -> ventaCoca()
                    else -> {
                        false
                    }

                }
            }
        }




        binding.btnEditarVenta.setOnClickListener {
            guardarVentaEditada(idEditar)
        }


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
                    Id: Long
                ) {
                    val productoSeleccionado = opcion[position]
                    binding.btnEditarProducto.setOnClickListener {
                        if (position == 0 && productoSeleccionado == "Lista Coca") {
                            Toast.makeText(
                                requireContext(),
                                "Debe seleccionar un producto",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            lifecycleScope.launch {
                                val precioProducto =
                                    ventaModel.obtenerPrecioCoca(productoSeleccionado)
                                guardarVenta(productoSeleccionado, precioProducto)
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
                ArrayAdapter<String>(requireContext(), R.layout.simple_spinner_item, opcion)
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
                            Toast.makeText(
                                requireContext(),
                                "Debe seleccionar un producto",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            lifecycleScope.launch {
                                val precio = ventaModel.obtenerPrecioBig(productoSeleccionado)
                                guardarVenta(productoSeleccionado, precio)
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

                            Toast.makeText(
                                requireContext(),
                                "Debe seleccionar un producto",
                                Toast.LENGTH_LONG
                            ).show()

                        } else {
                            lifecycleScope.launch {
                                val precioProducto =
                                    ventaModel.obtenerPrecioPrix(productoSeleccionado)
                                guardarVenta(productoSeleccionado, precioProducto)

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

    fun guardarVenta(productoSeleccionado: String, precioProducto: Double) {
        if (productoAgregado.containsKey(productoSeleccionado)) {
            val (cantidadExistente) = productoAgregado[productoSeleccionado]!!
            val nuevaCantidad = cantidadExistente + 1
            val nuevoPrecio = nuevaCantidad * precioProducto
            productoAgregado[productoSeleccionado] = Pair(nuevaCantidad, nuevoPrecio)

        } else {
            val cantidadPorDefecto = 1
            productoEditar.clear()
            productoAgregado[productoSeleccionado] = Pair(cantidadPorDefecto, precioProducto)

        }

        visualizarTabla()
    }

    private fun iniComponent() {
        tableLayou = binding.tlProductoEditar
        tableRow = binding.trProductoEditar
        chipGroup = binding.chListaEditar
    }

    private fun initData(idEditar: Int) {
        lifecycleScope.launch {
            ventaModel.obtenerDetalleEditarLiveData(idEditar)


            ventaModel.data.observe(viewLifecycleOwner) { detalleList ->
                detalleList?.let {
                    // Los datos se han actualizado, puedes proceder a visualizar la tabla
                    for (detalleItem in detalleList) {
                        val producto = detalleItem.producto
                        val cantidad = detalleItem.cantidad
                        val total = detalleItem.total_venta
                        guardarDatoRecuperado(idEditar, producto, cantidad, total)
                    }
                    visualizarTabla()
                }
            }
        }
    }


    private fun guardarDatoRecuperado(
        idParametro: Int,
        producto: String,
        cantidad: Int,
        total: Double
    ) {

        if (!productoEditar.containsKey(idParametro)) {
            productoEditar[idParametro] = Triple(producto, cantidad, total)
        }

    }


    private fun visualizarTabla() {
        tableLayou.removeAllViews()

        // Agregar elementos de productoEditar
        productoEditar.forEach { (pro, info) ->
            val producto = info.first
            val cantidad = info.second
            val total = info.third
            agregarFila(producto, cantidad, total, pro)
        }

        // Agregar elementos de productoAgregado
        productoAgregado.forEach { (producto, info) ->
            val cantidad = info.first
            val total = info.second
            agregarFila(producto, cantidad, total, -1)
        }
    }

    private fun agregarFila(producto: String, cantidad: Int, total: Double, pro: Int) {
        val tableRow = LayoutInflater.from(requireContext())
            .inflate(R.layout.table_row_venta, null) as TableRow

        val productoView = tableRow.findViewById<TextView>(R.id.tvProductoVenta)
        productoView.text = producto

        val cantidadView = tableRow.findViewById<TextView>(R.id.tvCantidadVenta)
        cantidadView.text = cantidad.toString()

        val precioView = tableRow.findViewById<TextView>(R.id.tvPrecioVenta)
        precioView.text = total.toString()

        productoView.setOnClickListener {
            if (cantidad >= 1) {
                val nuevaCantidad = cantidad - 1
                val newPrecio = nuevaCantidad * total / cantidad
                if (pro != -1) {
                    // Si es de productoEditar
                    productoEditar[pro] = Triple(producto, nuevaCantidad, newPrecio)
                } else {
                    // Si es de productoAgregado
                    productoAgregado[producto] = Pair(nuevaCantidad, newPrecio)
                }
                visualizarTabla()
            } else {
                if (pro != -1) {
                    // Si es de productoEditar
                    productoEditar.remove(pro)
                } else {
                    // Si es de productoAgregado
                    productoAgregado.remove(producto)
                }
                binding.tvTotalAmountEditar.text = 0.0.toString()
                visualizarTabla()
            }
        }

        cantidadView.setOnClickListener {
            if (cantidad >= 1) {
                val nuevaCantidad = cantidad + 1
                val newPrecio = nuevaCantidad * total / cantidad
                if (pro != -1) {
                    productoEditar[pro] = Triple(producto, nuevaCantidad, newPrecio)
                } else {
                    productoAgregado[producto] = Pair(nuevaCantidad, newPrecio)
                }
                visualizarTabla()
            } else {

                if (pro != -1) {
                    productoEditar.remove(pro)
                } else {
                    productoAgregado.remove(producto)
                }
                binding.tvTotalAmountEditar.text = 0.0.toString()
                visualizarTabla()
            }

        }

        tableLayou.addView(tableRow)
        binding.tvTotalAmountEditar.text = total.toString()
    }


    private fun guardarVentaEditada(idParametro: Int) {
        val fecha = args.fechaActual
        lifecycleScope.launch {
            for ((producto, info) in productoAgregado) {
                val cantidad = info.first
                val total = info.second
                val esVentaPorCajilla = binding.swVentaPorCajillaEditar.isChecked

                val venta = VentaPrixCoca(
                    idParametro,
                    producto,
                    total,
                    fecha,
                    System.currentTimeMillis(),
                    esVentaPorCajilla,
                    cantidad
                )
                ventaModel.editarVenta(venta)
            }

            for ((jr, info) in productoEditar) {
                val producto = info.first
                val cantidad = info.second
                val precio = info.third
                val esVentaPorCajilla = binding.swVentaPorCajillaEditar.isChecked

                val venta = VentaPrixCoca(
                    idParametro,
                    producto,
                    precio,
                    fecha,
                    System.currentTimeMillis(),
                    esVentaPorCajilla,
                    cantidad
                )
                ventaModel.editarVenta(venta)
            }
        }
        Toast.makeText(requireContext(), "Datos editado exitosamente", Toast.LENGTH_LONG)
            .show()
        requireActivity().supportFragmentManager.popBackStack()
    }


}