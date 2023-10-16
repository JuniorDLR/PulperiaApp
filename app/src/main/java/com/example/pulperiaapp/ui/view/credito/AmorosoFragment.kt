package com.example.pulperiaapp.ui.view.credito

import android.app.AlertDialog
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
import com.example.pulperiaapp.R
import com.example.pulperiaapp.data.database.entitie.credito.CreditoEntity
import com.example.pulperiaapp.databinding.FragmentAmorosoBinding
import com.example.pulperiaapp.ui.view.credito.viewmodel.ClienteViewModel
import com.example.pulperiaapp.ui.view.credito.viewmodel.CreditoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AmorosoFragment : Fragment() {
    private lateinit var binding: FragmentAmorosoBinding
    private val clienteViewModel: ClienteViewModel by viewModels()
    private val creditoViewModel: CreditoViewModel by viewModels()
    private val productoSeleccionados = mutableMapOf<String, Pair<Int, Double>>()
    private lateinit var tableLayout: TableLayout
    private lateinit var tableRow: TableRow


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAmorosoBinding.inflate(inflater, container, false)
        tableLayout = binding.tlProducto
        tableRow = binding.trProducto
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prixItem()
        cocaItem()
        lifecycleScope.launch {
            val autoComplete = binding.atvMoroso
            val lista = clienteViewModel.obtenerAmoros()

            val adapter = ArrayAdapter(requireContext(), R.layout.custom_autocomplete_item, lista)
            autoComplete.setAdapter(adapter)
        }

        binding.btnGuardarAmoroso.setOnClickListener {
            guardarVentaAmoroso()
        }
    }

    private fun guardarVentaAmoroso() {
        val amoroso = binding.atvMoroso.text.toString()
        val prodcutoVendida = mutableListOf<String>()
        val prodcutoCantidad = mutableListOf<String>()

        if (!amoroso.isEmpty()) {

            for ((prodcuto, info) in productoSeleccionados) {

                val cantidad = info.first
                val precio = info.second
                prodcutoVendida.add(prodcuto)
                prodcutoCantidad.add(cantidad.toString())

                val amorosoEntity = CreditoEntity(
                    0,
                    amoroso,
                    prodcutoVendida.joinToString(" , "),
                    prodcutoCantidad.joinToString(" , "),
                    precio,
                    System.currentTimeMillis(),
                    false
                )
                creditoViewModel.insertarCredito(amorosoEntity)
            }

            requireActivity().supportFragmentManager.popBackStack()
            Toast.makeText(requireContext(), "Datos guardados exitosamente", Toast.LENGTH_LONG)
                .show()
        } else {

            AlertDialog.Builder(requireContext()).setTitle("ADVERTENCIA")
                .setMessage("Debes de ingresar un amoroso")
                .setPositiveButton("Continuar") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }


    }

    private fun cocaItem() {
        lifecycleScope.launch {
            val spinnerCoca = binding.spCoca
            val lista = clienteViewModel.obtenerProductoCoca()
            val opcion = listOf("Seleccione un producto") + lista

            val adapter =
                ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, opcion)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinnerCoca.adapter = adapter




            spinnerCoca.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    itemSelected: AdapterView<*>?, p1: View?, position: Int, id: Long
                ) {
                    val productoSeleccionado = opcion[position]
                    binding.btnGuardarCoca.setOnClickListener {
                        if (position == 0 && productoSeleccionado == "Seleccione un producto") {
                            AlertDialog.Builder(requireContext()).setTitle("ADVERTENCIA")
                                .setMessage("Debe de seleccionar un producto")
                                .setPositiveButton("Continuar") { dialog, _ ->
                                    dialog.dismiss()
                                }.show()

                        } else {
                            lifecycleScope.launch {
                                val obtenerPrecio =
                                    clienteViewModel.obtenerPrecioCoca(productoSeleccionado)
                                guardarVenta(productoSeleccionado, obtenerPrecio)
                            }

                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
        }
    }


    private fun prixItem() {
        lifecycleScope.launch {
            val spinnerPrix = binding.spPrix

            val lista = clienteViewModel.obtenerProductoPrix()

            val opcion = listOf("Seleccione un producto") + lista
            val adapter =
                ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, opcion)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinnerPrix.adapter = adapter


            spinnerPrix.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    itemSleccionado: AdapterView<*>?, p1: View?, position: Int, id: Long
                ) {
                    val productoSeleccionado = opcion[position]
                    binding.btnGuardarPrix.setOnClickListener {

                        if (position == 0 && productoSeleccionado == "Seleccione un producto") {

                            AlertDialog.Builder(requireContext()).setTitle("ADVERTENCIA")
                                .setMessage("Debe de seleccionar un producto")
                                .setPositiveButton("Continuar") { dialog, _ ->
                                    dialog.dismiss()
                                }.show()

                        } else {
                            lifecycleScope.launch {
                                val precioProducto =
                                    clienteViewModel.obtenerPrecioPrix(productoSeleccionado)
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

    private fun guardarVenta(productoSeleccionado: String, obtenerPrecio: Double) {

        if (productoSeleccionados.containsKey(productoSeleccionado)) {
            val (cantidad, precio) = productoSeleccionados[productoSeleccionado]!!
            val nuevaCantidad = cantidad + 1
            val nuevoPrecio = nuevaCantidad * obtenerPrecio
            productoSeleccionados[productoSeleccionado] = Pair(nuevaCantidad, nuevoPrecio)

        } else {
            val cantidadPorDefecto = 1
            productoSeleccionados[productoSeleccionado] = Pair(cantidadPorDefecto, obtenerPrecio)

        }
        actualizarTabla()


    }

    private fun actualizarTabla() {
        tableLayout.removeAllViews()
        var precioTotal: Double = 0.0

        for ((producto, info) in productoSeleccionados) {
            tableRow = LayoutInflater.from(requireContext())
                .inflate(R.layout.tabla_row_item, null) as TableRow

            val cantidad = info.first
            val precio = info.second
            val productoView = tableRow.findViewById<TextView>(R.id.tvIdR)
            productoView.text = producto

            val cantidadView = tableRow.findViewById<TextView>(R.id.tvProductoR)
            cantidadView.text = cantidad.toString()


            val precioView = tableRow.findViewById<TextView>(R.id.tvPrecioR)
            precioView.text = precio.toString()

            tableLayout.addView(tableRow)


            precioTotal += precio

        }
        binding.tvTotalAmount.text = precioTotal.toString()

    }

}