package com.example.pulperiaapp.ui.view.filtrado

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.addCallback

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentFiltrarDatosBinding
import com.example.pulperiaapp.domain.venta.VentaPrixCocaDetalle
import com.example.pulperiaapp.ui.view.credito.adapter.AdapterAmoroso
import com.example.pulperiaapp.ui.view.credito.viewmodel.ClienteViewModel
import com.example.pulperiaapp.ui.view.credito.viewmodel.CreditoViewModel
import com.example.pulperiaapp.ui.view.principal.MainActivity
import com.example.pulperiaapp.ui.view.venta.adapter.AdapterVenta
import com.example.pulperiaapp.ui.view.venta.viewmodel.VentaViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class FiltrarDatosFragment : Fragment() {

    private lateinit var binding: FragmentFiltrarDatosBinding
    private val creditoViewModel: CreditoViewModel by viewModels()
    private val ventaViewModel: VentaViewModel by viewModels()
    private val clienteViewModel: ClienteViewModel by viewModels()
    private lateinit var adapterCredito: AdapterAmoroso
    private lateinit var adapterVenta: AdapterVenta
    private lateinit var adapterCliente: AdapterCliente
    private lateinit var recyclerView: RecyclerView
    private lateinit var tableLayout: TableLayout
    private var isCajilla: Boolean = false
    private var esIndividual: Boolean = false
    private var esMultiple: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFiltrarDatosBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initComponent(i: Int) {
        recyclerView = binding.rvFilter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        tableLayout = binding.tlProductoGanancia

        when (i) {
            1 -> adapterCredito = AdapterAmoroso(context = requireContext(), isEspecter = true)
            2 -> adapterVenta = AdapterVenta(onUpdateClickListener = { fecha, idProducto ->
                updateItem(fecha, idProducto)
            })

            3 -> {
                val decoratio =
                    DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
                adapterCliente = AdapterCliente()
                recyclerView.addItemDecoration(decoratio)
            }
        }
        recyclerView.adapter = when (i) {
            1 -> adapterCredito
            2 -> adapterVenta
            3 -> adapterCliente
            else -> null
        }

    }

    private fun updateItem(fecha: String, idProducto: Int) {
        findNavController().navigate(
            FiltrarDatosFragmentDirections.actionFiltrarDatosFragmentToEditarVentasFragment(
                idProducto = idProducto,
                idFecha = fecha,
                esIndividual = esIndividual,
                isMultiple = esMultiple,
                esFilter = true
            )
        )
    }

    private lateinit var selectedEditText: TextInputEditText

    private fun showDatePicker(editText: TextInputEditText) {
        selectedEditText = editText
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.MyDatePickerDialogTheme,
            { _, selectYear, selectMonth, selecteDay ->
                val selectedDay = Calendar.getInstance().apply {
                    set(selectYear, selectMonth, selecteDay)
                }
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedEditText.setText(dateFormat.format(selectedDay.time))
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callBack = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            cerrarSesion()
        }
        callBack.isEnabled = true

        binding.etFecha1.setupDatePicker()
        binding.etFecha2.setupDatePicker()

        binding.btnAplicarFiltros.setOnClickListener {
            val checkedRadioButtonId = binding.radioGroupClientes.checkedRadioButtonId
            if (checkedRadioButtonId == -1) {
                showAlertDialog("ADVERTENCIA", "Debes de seleccionar una opcion de filtrado")

            } else {
                when (binding.radioGroupClientes.checkedRadioButtonId) {
                    R.id.checkBoxPagado -> pagoRealizadoCliente()
                    R.id.radioButtonTodos -> filtrarVentas()
                    R.id.radioButtonRoom -> filtrarClientes()
                    R.id.radioButtonGanacia -> filtrarGanacia()
                }
            }

        }
        binding.radioGroupClientes.setOnCheckedChangeListener { _, idCheck ->
            when (idCheck) {
                R.id.checkBoxPagado -> {
                    with(binding) {
                        SwitchVisible.hide()
                        btnAplicarFiltros.show()
                        llGanacia.hide()
                        rvFilterS.show()
                    }
                    initComponent(1)
                }

                R.id.radioButtonTodos -> {
                    with(binding) {
                        etFecha1.isEnabled = true
                        etFecha2.isEnabled = true
                        SwitchVisible.show()
                        llGanacia.hide()
                        rvFilterS.show()
                        btnAplicarFiltros.show()
                    }
                    initComponent(2)
                }

                R.id.radioButtonRoom -> {
                    with(binding) {
                        etFecha1.isEnabled = false
                        etFecha2.isEnabled = false
                        llGanacia.hide()
                        SwitchVisible.hide()
                        rvFilterS.show()
                        btnAplicarFiltros.show()
                    }
                    initComponent(3)
                }

                R.id.radioButtonGanacia -> {
                    with(binding) {
                        etFecha1.isEnabled = true
                        etFecha2.isEnabled = true
                        llGanacia.show()
                        rvFilterS.hide()
                        SwitchVisible.hide()
                        btnAplicarFiltros.show()
                    }
                    initComponent(4)
                    tableLayout.removeAllViews()
                }
            }
        }

        binding.switchIndividual.setOnCheckedChangeListener { _, idChecked ->
            if (idChecked) binding.switchCajilla.isChecked = false
            isCajilla = false
        }

        binding.switchCajilla.setOnCheckedChangeListener { _, idChecked ->
            if (idChecked) {
                binding.switchIndividual.isChecked = false
                isCajilla = true
            }
        }
    }

    private fun View.show() {
        isVisible = true
    }

    private fun View.hide() {
        isVisible = false
    }

    private fun filtrarGanacia() {
        val fecha1 = binding.etFecha1.text.toString().trim()
        val fecha2 = binding.etFecha2.text.toString().trim()

        if (fecha1.isEmpty() || fecha2.isEmpty()) {
            showAlertDialog("ADVERTENCIA", "Las fechas no pueden quedar vacías")
        } else {
            lifecycleScope.launch {
                ventaViewModel.obtenerGananciasEntreFechas(fecha1, fecha2)
                val listaCajilla = ventaViewModel.obtenerFilterCajilla(fecha1, fecha2)
                val listaIndividual = ventaViewModel.obtenerFilterIndividual(fecha1, fecha2)
                cargarTabla(listaCajilla + listaIndividual, 4)
                ventaViewModel.obtenerGanacia.collect { ganancia ->
                    val precioFormateado = ganancia?.let { formatearPrecio(it) }
                    val textoView= "Ganancia: C\$$precioFormateado cordobas"
                    binding.tvGanancia.text = textoView
                }
            }
        }
    }

    private fun filtrarVentas() {
        val fecha1 = binding.etFecha1.text.toString().trim()
        val fecha2 = binding.etFecha2.text.toString().trim()

        if ((fecha1.isEmpty() || fecha2.isEmpty()) || (!binding.switchCajilla.isChecked && !binding.switchIndividual.isChecked)) {
            showAlertDialog(
                "ADVERTENCIA",
                "No has ingresado una fecha o no has elegido una opción de ventas"
            )
        } else {
            val viewModelScope = CoroutineScope(Dispatchers.Main)
            viewModelScope.launch {
                try {
                    if (isCajilla) {
                        ventaViewModel.obtenerFilterCajilla(fecha1, fecha2)
                    } else {
                        ventaViewModel.obtenerFilterIndividual(fecha1, fecha2)
                    }
                    cargarTabla(emptyList(), 2)
                } catch (e: Exception) {
                    showAlertDialog("Error", "Hubo un error al obtener los datos")
                }
            }
        }
    }

    private fun filtrarClientes() {
        val viewModelScope = CoroutineScope(Dispatchers.Main)
        viewModelScope.launch {
            clienteViewModel.obtenerAmoros()
        }
        CoroutineScope(Dispatchers.Main).launch {
            cargarTabla(emptyList(), 3)
        }
    }

    private fun pagoRealizadoCliente() {
        val viewModelScope = CoroutineScope(Dispatchers.Main)
        try {
            val fecha1 = binding.etFecha1.text.toString().trim()
            val fecha2 = binding.etFecha2.text.toString().trim()
            if (fecha1.isEmpty() || fecha2.isEmpty()) {
                showAlertDialog("ADVERTENCIA", "Las fechas no pueden quedar vacías")
            } else {
                viewModelScope.launch {
                    creditoViewModel.obtenerFilterPago(fecha1, fecha2)
                }
                CoroutineScope(Dispatchers.Main).launch {
                    cargarTabla(emptyList(), 1)
                }
            }
        } catch (e: Exception) {
            showAlertDialog("Error", "Hubo un error al obtener los datos")
        }
    }

    private fun TextInputEditText.setupDatePicker() {
        this.inputType = InputType.TYPE_NULL
        this.setOnClickListener { showDatePicker(this) }
    }

    private fun cargarTabla(listaCompleta: List<VentaPrixCocaDetalle>, indice: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            when (indice) {
                1 -> creditoViewModel.groupedAmorosoModel.observe(viewLifecycleOwner) { lista ->
                    adapterCredito.setLista(lista)
                    Log.e("Filtro", "filtrando pago realizado $lista")
                }

                2 -> {
                    if (isCajilla) {
                        ventaViewModel.ventaModelCajilla.collect { lista ->
                            adapterVenta.setListCajilla(lista)
                            Log.e("Filtro", "filtrando cajilla $lista")
                        }
                    } else {
                        ventaViewModel.ventaModelIndividual.collect { lista ->
                            adapterVenta.setListIndividual(lista)
                            Log.e("Filtro", "filtrando individual $lista")
                        }
                    }
                }

                3 -> clienteViewModel.clienteModel.observe(viewLifecycleOwner) { lista ->
                    adapterCliente.setList(lista)
                    Log.e("Filtro", "filtrando clientes $lista")
                }

                4 -> {
                    tableLayout.removeAllViews()
                    listaCompleta.forEach { lista ->
                        val producto = lista.producto
                        val cantidad = lista.cantidad
                        val precio = lista.totalVenta
                        visualizarTabla(producto, cantidad, precio)
                    }
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun visualizarTabla(producto: String, cantidad: Int, precio: Double) {
        val tableRow = LayoutInflater.from(requireContext())
            .inflate(R.layout.table_row_venta, null) as TableRow
        tableRow.findViewById<TextView>(R.id.tvProductoVenta).text = producto
        tableRow.findViewById<TextView>(R.id.tvCantidadVenta).text = cantidad.toString()
        val precioFormateado = formatearPrecio(precio)
        tableRow.findViewById<TextView>(R.id.tvPrecioVenta).text = precioFormateado
        tableLayout.addView(tableRow)
    }

    private fun formatearPrecio(precio: Double): String? {
        val bigDecimal = BigDecimal.valueOf(precio)
        val format = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.getDefault()))
        return format.format(bigDecimal)
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Continuar") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun cerrarSesion() {
        AlertDialog.Builder(requireContext())
            .setTitle("ADVERTENCIA")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Aceptar") { dialog, _ ->
                (activity as MainActivity).findViewById<BottomNavigationView>(R.id.NavigationBottom).isVisible =
                    false
                findNavController().navigate(R.id.action_filtrarDatosFragment_to_loginFragment)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
