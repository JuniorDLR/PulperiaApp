package com.example.pulperiaapp.ui.view.venta

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
import com.example.pulperiaapp.data.database.entitie.toDomain
import com.example.pulperiaapp.databinding.FragmentVentasProductoBinding
import com.example.pulperiaapp.ui.view.venta.viewmodel.VentaPrixCocaDetalle
import com.example.pulperiaapp.ui.view.venta.viewmodel.VentaViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VentasProductoFragment : Fragment() {

    private lateinit var binding: FragmentVentasProductoBinding
    private val ventaModel: VentaViewModel by viewModels()
    private lateinit var spinnerPrix: Spinner
    private lateinit var spinnerCoca: Spinner
    private lateinit var tableRow: TableRow
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
        spinnerPrix = binding.spPrix
        spinnerCoca = binding.spCoca
        tableLayout = binding.tlProducto
        tableRow = binding.trProducto
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ventaPrix()
        ventaCoca()
        binding.btnGuardarVenta.setOnClickListener { guardarProducto() }


    }

    private fun guardarProducto() {
        var totalVenta = 0.0
        val productosVendidos = mutableListOf<String>()
        val prodcutoCantidad = mutableListOf<String>()

        // Recopila los productos vendidos en una lista
        for (venta in productosSeleccionados) {
            productosVendidos.add(venta.key)
            totalVenta += venta.value.second
            prodcutoCantidad.add(venta.value.first.toString())
        }

        val ventaConProductos = VentaPrixCocaDetalle(
            VentaPrixCoca(
                0,
                productosVendidos.joinToString(","),
                totalVenta,
                System.currentTimeMillis(),
                prodcutoCantidad.joinToString(",")
            )
        )
        val ventaPrixCoca = ventaConProductos.toDomain()
        ventaModel.insertarVenta(ventaPrixCoca)

        Toast.makeText(requireContext(), "Datos guardados exitosamente", Toast.LENGTH_LONG).show()
        requireActivity().supportFragmentManager.popBackStack()
    }


    private fun ventaPrix() {
        lifecycleScope.launch {
            val spinnerPrix = binding.spPrix
            val listaString = ventaModel.obtenerProdcutoPrix()
            val opcion = listOf("Seleccione un producto") + listaString

            //se utiliza para asociar una matriz de datos (en este caso, listaString)
            val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, opcion)

            //establecer el diseño que se utilizará para mostrar las opciones cuando el Spinner se despliegue como una lista.
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
                    binding.btnGuardarPrix.setOnClickListener {

                        if (position == 0 && productoSeleccionado == "Seleccione un producto") {
                            Log.d("Seleccion", "Producto seleccionado: $productoSeleccionado")
                            Toast.makeText(
                                requireContext(),
                                "Debe seleccionar un producto",
                                Toast.LENGTH_LONG
                            ).show()

                        } else {
                            Log.e("Error", "Índice fuera de los límites: $position")
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
            val spinnerCoca = binding.spCoca
            val listaString = ventaModel.obtenerProductoCoca()

            val opcion = listOf("Seleccione un producto") + listaString

            //se utiliza para asociar una matriz de datos (en este caso, listaString)
            val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, opcion)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinnerCoca.adapter = adapter


            spinnerCoca.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    itemSelected: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    id: Long
                ) {
                    val productoSeleccionado = opcion[position]
                    binding.btnGuardarCoca.setOnClickListener {

                        if (position == 0 && productoSeleccionado == "Seleccione un producto") {
                            Log.d("Seleccion", "Producto seleccionado: $productoSeleccionado")
                            Toast.makeText(
                                requireContext(),
                                "Debe seleccionar un producto",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Log.e("Error", "Índice fuera de los límites: $position")
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
                .inflate(R.layout.tabla_row_item, null) as TableRow

            val productoView = tableRow.findViewById<TextView>(R.id.tvIdR)
            productoView.text = producto

            val cantidadView = tableRow.findViewById<TextView>(R.id.tvProductoR)
            cantidadView.text = cantidad.toString()

            val precioView = tableRow.findViewById<TextView>(R.id.tvPrecioR)
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