package com.example.pulperiaapp.ui.view.venta.view


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentVentasBinding
import com.example.pulperiaapp.ui.view.principal.MainActivity
import com.example.pulperiaapp.ui.view.venta.adapter.AdapterVenta
import com.example.pulperiaapp.ui.view.venta.viewmodel.VentaViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class VentasFragment : Fragment() {

    private val ventasModel: VentaViewModel by viewModels()
    private lateinit var binding: FragmentVentasBinding
    private lateinit var recyclerView: RecyclerView
    lateinit var adapterVenta: AdapterVenta
    private lateinit var tabLayout: TabLayout
    private var esIndividual: Boolean = false
    private var currentTabPosition: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVentasBinding.inflate(inflater, container, false)



        binding.root.postDelayed({
            tabLayout.getTabAt(1)?.select()
        }, 100)


        binding.btnAgregarVenta.setColorFilter(
            ContextCompat.getColor(
                requireContext(), R.color.white
            )
        )


        return binding.root
    }


    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponent()
        val fechaInicio = obtenerFechaInicioActual()
        val fechaFin = obtenerFechaFin()

        binding.etFecha.inputType = InputType.TYPE_NULL
        binding.etFecha.setOnClickListener { showDateTimePicker() }

        lifecycleScope.launch {
            ventasModel.obtenerTotal.collect { total ->
               val precioFormateado = total?.let { formatearPrecio(it) }
                val ganancia = "Ganancia: $precioFormateado"
                binding.tvTotalVenta.text = ganancia
            }
        }



        ventasModel.obtenerTotal(fechaInicio, fechaFin)
        binding.btnAgregarVenta.setOnClickListener {
            val action = VentasFragmentDirections.actionVentasFragmentToVentasProductoFragment()
            Navigation.findNavController(binding.root).navigate(action)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapterVenta.filter.filter(p0)
            }

            override fun afterTextChanged(p0: Editable?) {


            }

        }

        binding.etFecha.addTextChangedListener(textWatcher)


        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    currentTabPosition = it.position
                    adapterVenta.verificacion(currentTabPosition)
                    lifecycleScope.launch {
                        when (currentTabPosition) {

                            0 -> {
                                esIndividual = true
                                try {

                                    ventasModel.obtenerVentaIndividual(fechaInicio, fechaFin)
                                    ventasModel.ventaModelIndividual.collect { lista ->
                                        adapterVenta.setListIndividual(lista)

                                    }

                                } catch (e: Exception) {
                                    Log.e("TAG", "Error al obtener venta individual: ${e.message}")
                                }


                            }

                            1 -> {
                                esIndividual = false
                                try {

                                    ventasModel.obtenerVentaCajilla(fechaInicio, fechaFin)
                                    ventasModel.ventaModelCajilla.collect { lista ->
                                        adapterVenta.setListCajilla(lista)

                                    }



                                } catch (e: Exception) {
                                    Log.e(
                                        "TAG",
                                        "Error al obtener venta de la cajilla: ${e.message}"
                                    )
                                }

                            }
                        }
                    }


                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })


        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            cerrarSesion()
        }
        callback.isEnabled = true

    }


    private fun initComponent() {


        recyclerView = binding.rvVentas
        tabLayout = binding.tabLayout
        val linear = LinearLayoutManager(requireContext())
        linear.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linear



        adapterVenta = AdapterVenta(
            onUpdateClickListener = { fecha, idProducto ->
                onUpdateItem(
                    fecha,
                    idProducto
                )
            },
            onDeleteClickListener = { idProducto, position, fecha ->
                onDeleteItem(
                    idProducto,
                    position,
                    fecha
                )
            })
        recyclerView.adapter = adapterVenta


    }


    @SuppressLint("SimpleDateFormat")
    private fun obtenerFechaInicioActual(): String {
        val fechaActual = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fechaActual.time)
    }

    private fun formatearPrecio(precio: Double): String? {
        val bigDecimal = BigDecimal.valueOf(precio)
        val format = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.getDefault()))
        return format.format(bigDecimal)
    }
    @SuppressLint("SimpleDateFormat")
    private fun obtenerFechaFin(): String {
        val fechaActual = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fechaActual.time)
    }


    private fun cerrarSesion() {

        val alert = AlertDialog.Builder(requireContext())
            .setTitle("ADVERTENCIA")
            .setMessage("¿Estas seguro que desea cerrar sesion?")
            .setPositiveButton("Aceptar") { dialog, _ ->

                (activity as MainActivity).findViewById<BottomNavigationView>(R.id.NavigationBottom).isVisible =
                    false

                findNavController().navigate(R.id.action_ventasFragment_to_loginFragment)
                dialog.dismiss()

            }

            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

        alert.show()

    }

    private fun onDeleteItem(idProducto: Int, position: Int, fechaAgrupada: String) {
        val fechaInicio = obtenerFechaInicioActual()
        val fechaFin = obtenerFechaFin()

        AlertDialog.Builder(requireContext())
            .setTitle("ADVERTENCIA")
            .setMessage("¿Desea eliminar esta venta?")
            .setPositiveButton("Si") { dialog, _ ->
                lifecycleScope.launch {
                    try {
                        if (esIndividual) {
                            ventasModel.eliminarVenta(idProducto)
                        } else {
                            val ventasCajilla =
                                ventasModel.obtenerVentaCajilla(fechaInicio, fechaFin)
                            val ventasFiltradas =
                                ventasCajilla.filter { it.fechaVenta == fechaAgrupada }
                            for (venta in ventasFiltradas) {
                                ventasModel.eliminarVenta(venta.id)
                            }
                        }
                        // Eliminar el elemento de la lista de datos del adaptador
                        adapterVenta.removeItem(position)
                        // Notificar al adaptador sobre el cambio específico
                        adapterVenta.notifyItemRemoved(position)
                        Toast.makeText(
                            requireContext(),
                            "Venta eliminada exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        ventasModel.obtenerTotal(fechaInicio, fechaFin)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Manejar la excepción según tus necesidades
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("No") { diag, _ ->
                diag.dismiss()
            }
            .show()
    }

    private fun onUpdateItem(fechaVenta: String, idProducto: Int) {
        findNavController().navigate(
            VentasFragmentDirections.actionVentasFragmentToEditarVentasFragment(
                idProducto = idProducto,
                idFecha = fechaVenta,
                esIndividual = esIndividual,
                isMultiple = false,
                esFilter = false
            )
        )
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            R.style.MyDatePickerDialogTheme,
            { _, selectedHour, selectedMinute ->
                val selectedDateTime = Calendar.getInstance()
                selectedDateTime.set(year, month, day, selectedHour, selectedMinute)

                // Formatear la fecha y hora según tus necesidades
                val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val formattedDateTime = dateTimeFormat.format(selectedDateTime.time)

                // Establecer el resultado en tu EditText u otro componente de interfaz de usuario
                binding.etFecha.setText(formattedDateTime)

            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

}