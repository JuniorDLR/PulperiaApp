package com.example.pulperiaapp.ui.view.venta

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.pulperiaapp.R
import com.example.pulperiaapp.data.database.entitie.VentaPrixCoca
import com.example.pulperiaapp.databinding.FragmentVentasProductoBinding
import com.example.pulperiaapp.ui.view.venta.viewmodel.VentaViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VentasProductoFragment : Fragment() {

    private lateinit var binding: FragmentVentasProductoBinding
    private val ventaModel: VentaViewModel by viewModels()
    private lateinit var spinnerProducto: Spinner
    private lateinit var tableRow: TableRow
    private lateinit var chipGroup: ChipGroup
    private lateinit var tableLayout: TableLayout
    private val productosSeleccionados = mutableMapOf<String, Pair<Int, Double>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentVentasProductoBinding.inflate(inflater, container, false)
        initComponent()
        return binding.root

    }

    private fun initComponent() {
        chipGroup = binding.chLista
        spinnerProducto = binding.spProductos
        tableLayout = binding.tlProducto
        tableRow = binding.trProducto
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chipGroup.setOnCheckedStateChangeListener { group, checkId ->
            for (idList in checkId) {
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
        binding.btnGuardarVenta.setOnClickListener { guardarProducto() }


    }

    private fun ventaBigCola() {
        lifecycleScope.launch {
            val spinner = binding.spProductos
            val lista = ventaModel.obtenerProductoBig()
            val listaConcatenada = listOf("Lista Big cola") + lista

            val adapter =
                ArrayAdapter<String>(
                    requireContext(),
                    R.layout.simple_spinner_item,
                    listaConcatenada
                )
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    id: Long
                ) {

                    val productoSeleccionado = listaConcatenada[position]
                    binding.btnGuardarProducto.setOnClickListener {
                        if (position == 0 && productoSeleccionado == "Lista Big cola") {
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
                    TODO("Not yet implemented")
                }

            }
        }


    }

    private fun guardarProducto() {
        var totalVenta = 0.0
        if (productosSeleccionados.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Tiene que ingresar productos para guardar",
                Toast.LENGTH_LONG
            )
                .show()

        } else {
            for (venta in productosSeleccionados) {
                val productoVendido = venta.key
                val cantidad = venta.value.first
                totalVenta += venta.value.second
                if (binding.swVentaPorCajilla.isChecked) {
                    val ventaConProductos = VentaPrixCoca(
                        id = 0,
                        producto = productoVendido,
                        total = totalVenta,
                        fecha = System.currentTimeMillis(),
                        cantidad = cantidad,
                        ventaPorCajilla = true
                    )

                    ventaModel.insertarVenta(ventaConProductos)
                } else {
                    val ventaConProductos =
                        VentaPrixCoca(
                            id = 0,
                            producto = productoVendido,
                            total = totalVenta,
                            fecha = System.currentTimeMillis(),
                            cantidad = cantidad,
                            ventaPorCajilla = false
                        )
                    ventaModel.insertarVenta(ventaConProductos)
                }
            }

            Toast.makeText(requireContext(), "Datos guardados exitosamente", Toast.LENGTH_LONG)
                .show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }


    private fun ventaPrix() {
        lifecycleScope.launch {
            val spinnerPrix = binding.spProductos
            val listaString = ventaModel.obtenerProdcutoPrix()
            val opcion = listOf("Lista prix") + listaString

            val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, opcion)

            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinnerPrix.adapter = adapter


            spinnerPrix.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    id: Long
                ) {
                    val productoSeleccionado = opcion[position]
                    binding.btnGuardarProducto.setOnClickListener {

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

    private fun ventaCoca() {


        lifecycleScope.launch {
            val spinnerCoca = binding.spProductos
            val listaString = ventaModel.obtenerProductoCoca()

            val opcion = listOf("Lista Coca") + listaString


            val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, opcion)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinnerCoca.adapter = adapter


            spinnerCoca.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    itemSelected: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    id: Long
                ) {
                    val productoSeleccionado = opcion[position]
                    binding.btnGuardarProducto.setOnClickListener {

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

                override fun onNothingSelected(noItemSelected: AdapterView<*>?) {
                    Log.d("Seleccion", "Ningún producto seleccionado")

                }

            }
        }


    }


    fun guardarVenta(productoSeleccionado: String, precioProducto: Double) {
        if (productosSeleccionados.containsKey(productoSeleccionado)) {
            val (cantidadExistente) = productosSeleccionados[productoSeleccionado]!!
            val nuevaCantidad = cantidadExistente + 1
            val nuevoPrecio =
                nuevaCantidad * precioProducto
            productosSeleccionados[productoSeleccionado] = Pair(nuevaCantidad, nuevoPrecio)
        } else {
            val cantidadPorDefecto = 1
            productosSeleccionados[productoSeleccionado] = Pair(cantidadPorDefecto, precioProducto)
        }
        actualizarTabla()
    }


    @SuppressLint("InflateParams")
    private fun actualizarTabla() {
        tableLayout.removeAllViews()

        var precioTotal = 0.0
        for ((producto, info) in productosSeleccionados) {
            val cantidad = info.first
            val precio = info.second

            val tableRow = LayoutInflater.from(requireContext())
                .inflate(R.layout.table_row_venta, null) as TableRow

            val productoView = tableRow.findViewById<TextView>(R.id.tvProductoVenta)
            productoView.text = producto

            val cantidadView = tableRow.findViewById<TextView>(R.id.tvCantidadVenta)
            cantidadView.text = cantidad.toString()

            val precioView = tableRow.findViewById<TextView>(R.id.tvPrecioVenta)
            precioView.text = precio.toString()



            productoView.setOnClickListener {
                if (cantidad > 1) {
                    val newCantidad = cantidad - 1
                    val newPrecio = newCantidad * precio / cantidad
                    productosSeleccionados[producto] = Pair(newCantidad, newPrecio)
                    actualizarTabla()


                } else {
                    productosSeleccionados.remove(producto)
                    actualizarTabla()
                }
            }



            tableLayout.addView(tableRow)
            precioTotal += precio
        }

        binding.tvTotalAmount.text = precioTotal.toString()
    }


}