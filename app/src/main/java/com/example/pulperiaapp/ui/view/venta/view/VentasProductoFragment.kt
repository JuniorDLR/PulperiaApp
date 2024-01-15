package com.example.pulperiaapp.ui.view.venta.view

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
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.pulperiaapp.R
import com.example.pulperiaapp.data.database.entitie.VentaPrixCoca
import com.example.pulperiaapp.databinding.FragmentVentasProductoBinding
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
class VentasProductoFragment : Fragment() {

    private lateinit var binding: FragmentVentasProductoBinding
    private val ventaModel: VentaViewModel by viewModels()
    private lateinit var spinnerProducto: Spinner
    private lateinit var tableRow: TableRow
    private lateinit var chipGroup: ChipGroup
    private lateinit var tableLayout: TableLayout
    private val productosSeleccionados = mutableMapOf<String, Pair<Int, Double>>()
    var productoClave: String = ""
    val respaldo = LinkedHashMap<String, Pair<Int, Double>>()

    var esRespaldo: Boolean = false


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

        binding.inputPrecio.isEnabled = false

        binding.swVentaPorCajilla.setOnCheckedChangeListener { _, isCheked ->
            if (isCheked) {
                binding.inputPrecio.isEnabled = true
            } else {
                binding.inputPrecio.isEnabled = false
            }
        }


        chipGroup.setOnCheckedStateChangeListener { group, checkId ->
            for (idList in checkId) {
                val selected = group.findViewById(idList) as Chip
                when (selected.tag) {
                    "Prixcola" -> ventaPrix()
                    "BigCola" -> ventaBigCola()
                    "Coca" -> ventaCoca()
                    else -> {
                        Toast.makeText(
                            requireContext(),
                            "Seleccion no valida",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
            }

        }
        binding.btnGuardarVenta.setOnClickListener { guardarProducto() }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_ventasProductoFragment_to_ventasFragment)
        }




        binding.tvPrecioPersonalizado.doOnTextChanged { text, _, _, _ ->

            if (text!!.length >= 5) {
                binding.inputPrecio.error = "Limite de caracteres alcanzado"
            } else {
                binding.inputPrecio.error = null
            }
        }

        binding.tvPrecioPersonalizado.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                respaldo.clear()
                respaldo.putAll(productosSeleccionados)
            }


            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(texto: Editable?) {
                val precioPersonalizado = texto.toString().toDoubleOrNull()
                actualizarEnTiempoReal(precioPersonalizado)
            }


        })

    }


    private fun actualizarEnTiempoReal(precioPersonalizado: Double?) {

        if (precioPersonalizado != null && binding.swVentaPorCajilla.isChecked) {
            esRespaldo = true

            val cantidadTotal = respaldo.values.sumOf { it.first }
            val precioPorUnidad = precioPersonalizado / cantidadTotal

            for ((productoClave, valor) in respaldo) {
                val cantidad = valor.first
                val nuevoPrecio = cantidad * precioPorUnidad
                respaldo[productoClave] = Pair(cantidad, nuevoPrecio)
            }


            actualizarTabla(esRespaldo)
        } else {
            respaldo.clear()
            actualizarTabla(false)
        }
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
                            Snackbar.make(
                                requireView(),
                                "Debes de seleccionar un producto",
                                Snackbar.LENGTH_LONG
                            ).show()
                        } else {
                            lifecycleScope.launch {
                                val precio = ventaModel.obtenerPrecioBig(productoSeleccionado)
                                if (precio != null) {
                                    guardarVenta(productoSeleccionado, precio)
                                }
                            }

                        }

                    }

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {


                }

            }
        }


    }


    private fun guardarProducto() {
        if (productosSeleccionados.isEmpty()) {
            Snackbar.make(
                requireView(),
                "No hay datos en la tabla",
                Snackbar.LENGTH_LONG
            ).show()

        } else {

            val mapaEjecutar = if (esRespaldo) respaldo else productosSeleccionados

            for (venta in mapaEjecutar) {
                val productoVendido = venta.key
                val cantidad = venta.value.first
                val total = venta.value.second
                val ventaPorCajilla = binding.swVentaPorCajilla.isChecked
                val fechaFormateada =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val ventaConProductos = VentaPrixCoca(
                    id = 0,
                    producto = productoVendido,
                    total = total,
                    fecha = fechaFormateada,
                    cantidad = cantidad,
                    ventaPorCajilla = ventaPorCajilla
                )

                ventaModel.insertarVenta(ventaConProductos)

            }

            Toast.makeText(requireContext(), "Datos guardados exitosamente!!", Toast.LENGTH_LONG)
                .show()
            val action =
                VentasProductoFragmentDirections.actionVentasProductoFragmentToVentasFragment()
            Navigation.findNavController(binding.root).navigate(action)


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
                            Snackbar.make(
                                requireView(),
                                "Debes de seleccionar un producto",
                                Snackbar.LENGTH_LONG
                            ).show()

                        } else {
                            lifecycleScope.launch {
                                val precioProducto =
                                    ventaModel.obtenerPrecioPrix(productoSeleccionado)
                                if (precioProducto != null) {
                                    guardarVenta(productoSeleccionado, precioProducto)
                                }

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
                            Snackbar.make(
                                requireView(),
                                "Debes de seleccionar un producto",
                                Snackbar.LENGTH_LONG
                            ).show()
                        } else {

                            lifecycleScope.launch {
                                val precioProducto =
                                    ventaModel.obtenerPrecioCoca(productoSeleccionado)
                                if (precioProducto != null) {
                                    guardarVenta(productoSeleccionado, precioProducto)
                                }
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
        productoClave = productoSeleccionado
        if (productosSeleccionados.containsKey(productoSeleccionado)) {

            val (cantidadExistente) = productosSeleccionados[productoSeleccionado]!!
            val nuevaCantidad = cantidadExistente + 1
            val nuevoPrecio = nuevaCantidad * precioProducto
            productosSeleccionados[productoSeleccionado] = Pair(nuevaCantidad, nuevoPrecio)

        } else {

            val cantidadPorDefecto = 1
            productosSeleccionados[productoSeleccionado] = Pair(cantidadPorDefecto, precioProducto)

        }

        actualizarTabla(false)
    }

    private fun actualizarTabla(esRespaldo: Boolean) {
        tableLayout.removeAllViews()
        var precioTotal = 0.0

        if (esRespaldo) {
            for ((producto, info) in respaldo) {
                val cantidad = info.first
                val precio = info.second
                visualizarTabla(producto, cantidad, precio)
                precioTotal += precio
            }

        } else {
            for ((producto, info) in productosSeleccionados) {
                val cantidad = info.first
                val precio = info.second
                visualizarTabla(producto, cantidad, precio)
                precioTotal += precio
            }
        }

        val precioFormateado = formatearPrecio(precioTotal)
        binding.tvTotalAmount.text = precioFormateado
    }


    private fun visualizarTabla(producto: String, cantidad: Int, precio: Double) {


        val tableRow = LayoutInflater.from(requireContext())
            .inflate(R.layout.table_row_venta, null) as TableRow

        val productoView = tableRow.findViewById<TextView>(R.id.tvProductoVenta)
        productoView.text = producto

        val cantidadView = tableRow.findViewById<TextView>(R.id.tvCantidadVenta)
        cantidadView.text = cantidad.toString()

        val precioView = tableRow.findViewById<TextView>(R.id.tvPrecioVenta)


        val precioFormateado = formatearPrecio(precio)
        precioView.text = precioFormateado


        cantidadView.setOnClickListener {

            showQuantityDialog(producto, precio, cantidadView, cantidad)
        }



        tableLayout.addView(tableRow)

    }

    private fun showQuantityDialog(
        producto: String,
        precio: Double,
        cantidadView: TextView,
        cantidad: Int
    ) {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Modificar cantidad")
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.setText(cantidadView.text)

        builder.setView(input)

        builder.setPositiveButton("Aceptar") { dialog, _ ->

            val nuevaCantidad = input.text.toString().toInt()
            val nuevoPrecio = nuevaCantidad * precio / cantidad

            productosSeleccionados[producto] = Pair(nuevaCantidad, nuevoPrecio)
            actualizarTabla(false)

            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()

        }
        builder.setNeutralButton("ELIMINAR") { dialog, _ ->
            productosSeleccionados.remove(producto)
            actualizarTabla(false)
            dialog.dismiss()
        }

        builder.show()


    }
    private fun formatearPrecio(precio: Double): String? {
        val bigDecimal = BigDecimal.valueOf(precio)
        val format = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.getDefault()))
        return format.format(bigDecimal)
    }

}