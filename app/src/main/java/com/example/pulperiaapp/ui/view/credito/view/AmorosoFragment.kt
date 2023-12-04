package com.example.pulperiaapp.ui.view.credito.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
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
import com.example.pulperiaapp.data.database.entitie.CreditoEntity
import com.example.pulperiaapp.databinding.FragmentAmorosoBinding
import com.example.pulperiaapp.ui.view.credito.viewmodel.ClienteViewModel
import com.example.pulperiaapp.ui.view.credito.viewmodel.CreditoViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AmorosoFragment : Fragment() {
    private lateinit var binding: FragmentAmorosoBinding
    private val clienteViewModel: ClienteViewModel by viewModels()
    private val creditoViewModel: CreditoViewModel by viewModels()
    private val productoSeleccionados = mutableMapOf<String, Pair<Int, Double>>()
    private lateinit var tableLayout: TableLayout
    private lateinit var tableRow: TableRow
    private lateinit var chipGroup: ChipGroup


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentAmorosoBinding.inflate(inflater, container, false)
        initComponent()

        return binding.root
    }

    private fun initComponent() {
        tableLayout = binding.tlProducto
        tableRow = binding.trProducto
        chipGroup = binding.chLista
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val autoComplete = binding.atvMoroso
            val lista = clienteViewModel.obtenerAmoros()

            val adapter = ArrayAdapter(requireContext(), R.layout.custom_autocomplete_item, lista)
            autoComplete.setAdapter(adapter)
        }

        chipGroup.setOnCheckedStateChangeListener { group, idCheck ->
            for (lista in idCheck) {
                val selected = group.findViewById(lista) as Chip
                when (selected.tag) {
                    "Prixcola" -> prixItem()
                    "BigCola" -> bigItem()
                    "Coca" -> cocaItem()
                    else -> {
                        false
                    }


                }
            }
        }

        binding.btnGuardarAmoroso.setOnClickListener {
            guardarVentaAmoroso()
        }

    }

    private fun bigItem() {
        lifecycleScope.launch {
            val spinner = binding.spProdutos
            val producto = clienteViewModel.obtenerProductoBig()
            val lista = listOf("Lista big") + producto
            val adapter =
                ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, lista)
            adapter.setDropDownViewResource(R.layout.simple_spinner_item)
            spinner.adapter = adapter



            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    id: Long
                ) {
                    val productoSeleccionado = lista[position]
                    binding.btnGuardarProducto.setOnClickListener {
                        if (position == 0 && productoSeleccionado == "Lista big") {
                            AlertDialog.Builder(requireContext()).setTitle("ADVERTENCIA")
                                .setMessage("Debe de seleccionar un producto")
                                .setPositiveButton("Continuar") { dialog, _ ->
                                    dialog.dismiss()
                                }.show()

                        } else {
                            lifecycleScope.launch {
                                val obtenerPrecio =
                                    clienteViewModel.obtenerPrecioBig(productoSeleccionado)
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

    private fun guardarVentaAmoroso() {

        val fechaFormateada =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val amoroso = binding.atvMoroso.text.toString()
        val amorosoEntities = mutableListOf<CreditoEntity>()
        if (amoroso.isNotEmpty() && productoSeleccionados.isNotEmpty()) {


            for ((producto, info) in productoSeleccionados) {
                val cantidad = info.first
                val precio = info.second

                val amorosoEntity = CreditoEntity(
                    0,
                    amoroso,
                    producto,
                    cantidad,
                    precio,
                    fechaFormateada,
                    false
                )

                amorosoEntities.add(amorosoEntity)
            }

            // Insertar los créditos en el ViewModel
            creditoViewModel.insertarCredito(amorosoEntities)

            requireActivity().supportFragmentManager.popBackStack()
            Toast.makeText(requireContext(), "Datos guardados exitosamente", Toast.LENGTH_LONG)
                .show()
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("ADVERTENCIA")
                .setMessage("No has ingresado un amoroso o la tabla esta vacia.")
                .setPositiveButton("Continuar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }


    private fun cocaItem() {
        lifecycleScope.launch {
            val spinnerCoca = binding.spProdutos
            val lista = clienteViewModel.obtenerProductoCoca()
            val opcion = listOf("Lista coca") + lista

            val adapter =
                ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, opcion)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinnerCoca.adapter = adapter




            spinnerCoca.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    itemSelected: AdapterView<*>?, p1: View?, position: Int, id: Long
                ) {
                    val productoSeleccionado = opcion[position]
                    binding.btnGuardarProducto.setOnClickListener {
                        if (position == 0 && productoSeleccionado == "Lista coca") {
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
            val spinnerPrix = binding.spProdutos

            val lista = clienteViewModel.obtenerProductoPrix()

            val opcion = listOf("Lista prix") + lista
            val adapter =
                ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, opcion)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinnerPrix.adapter = adapter


            spinnerPrix.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    itemSleccionado: AdapterView<*>?, p1: View?, position: Int, id: Long
                ) {
                    val productoSeleccionado = opcion[position]
                    binding.btnGuardarProducto.setOnClickListener {

                        if (position == 0 && productoSeleccionado == "Lista prix") {

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
            val (cantidad) = productoSeleccionados[productoSeleccionado]!!
            val nuevaCantidad = cantidad + 1
            val nuevoPrecio = nuevaCantidad * obtenerPrecio
            productoSeleccionados[productoSeleccionado] = Pair(nuevaCantidad, nuevoPrecio)

        } else {
            val cantidadPorDefecto = 1
            productoSeleccionados[productoSeleccionado] = Pair(cantidadPorDefecto, obtenerPrecio)

        }
        actualizarTabla()


    }

    @SuppressLint("InflateParams")
    private fun actualizarTabla() {
        tableLayout.removeAllViews()
        var precioTotal = 0.0

        for ((producto, info) in productoSeleccionados) {
            tableRow = LayoutInflater.from(requireContext())
                .inflate(R.layout.table_row_venta, null) as TableRow

            val cantidad = info.first
            val precio = info.second
            val productoView = tableRow.findViewById<TextView>(R.id.tvProductoVenta)
            productoView.text = producto

            val cantidadView = tableRow.findViewById<TextView>(R.id.tvCantidadVenta)
            cantidadView.text = cantidad.toString()


            val precioView = tableRow.findViewById<TextView>(R.id.tvPrecioVenta)
            precioView.text = precio.toString()

            tableLayout.addView(tableRow)


            precioTotal += precio

        }
        binding.tvTotalAmount.text = precioTotal.toString()

    }

}