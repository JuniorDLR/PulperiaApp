package com.example.pulperiaapp.ui.view.filtrado

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentFiltrarDatosBinding
import com.example.pulperiaapp.ui.view.credito.adapter.AdapterAmoroso
import com.example.pulperiaapp.ui.view.credito.viewmodel.ClienteViewModel
import com.example.pulperiaapp.ui.view.credito.viewmodel.CreditoViewModel
import com.example.pulperiaapp.ui.view.principal.MainActivity
import com.example.pulperiaapp.ui.view.venta.adapter.AdapterVenta
import com.example.pulperiaapp.ui.view.venta.viewmodel.VentaViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


@AndroidEntryPoint
class FiltrarDatosFragment : Fragment() {

    private lateinit var binding: FragmentFiltrarDatosBinding
    private val creditoViewModel: CreditoViewModel by viewModels()
    private val ventaViewModel: VentaViewModel by viewModels()
    private val clienteViewModel: ClienteViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterVenta: AdapterVenta
    private lateinit var adapterCredito: AdapterAmoroso
    private lateinit var adapterCliente: AdapterCliente
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
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager

        when (i) {
            1 -> {
                val isEspecter = true
                adapterCredito = AdapterAmoroso(context = requireContext(), isEspecter = isEspecter)
                recyclerView.adapter = adapterCredito
            }

            2 -> {

                adapterVenta = AdapterVenta(onUpdateClickListener = { fecha, idProducto ->
                    updateItem(fecha, idProducto)
                })
                recyclerView.adapter = adapterVenta
            }

            3 -> {
                val decoratio =
                    DividerItemDecoration(requireContext(), linearLayoutManager.orientation)
                adapterCliente = AdapterCliente()
                recyclerView.adapter = adapterCliente
                recyclerView.addItemDecoration(decoratio)

            }
        }


    }

    private fun updateItem(fecha: String, idProducto: Int) {
        findNavController().navigate(
            FiltrarDatosFragmentDirections.actionFiltrarDatosFragmentToEditarVentasFragment(
                idProducto = idProducto,
                idFecha = fecha,
                esIndividual = esIndividual,
                isMultiple = esMultiple
            )
        )
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.MyDatePickerDialogTheme,
            { _, selectYear, selectMonth, selecteDay ->
                val selectDay = Calendar.getInstance()
                selectDay.set(selectYear, selectMonth, selecteDay)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formateDate = dateFormat.format(selectDay.time)
                binding.etFecha.setText(formateDate)

            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callBack = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            cerrarSesion()
        }
        callBack.isEnabled = true

        binding.etFecha.setOnClickListener { showDatePicker() }

        val switchIndividual = binding.switchIndividual
        val switchCajilla = binding.switchCajilla

        binding.radioGroupClientes.setOnCheckedChangeListener { group, idCheck ->

            when (idCheck) {
                R.id.checkBoxPagado -> {
                    binding.etFecha.isEnabled = false
                    binding.SwitchVisible.isVisible = false
                    filtrarDato(1)
                    binding.btnAplicarFiltros.isVisible = false
                }

                R.id.radioButtonTodos -> {
                    binding.etFecha.isEnabled = true
                    binding.SwitchVisible.isVisible = true
                    filtrarDato(2)
                    binding.btnAplicarFiltros.isVisible = true
                }

                R.id.radioButtonRoom -> {
                    binding.etFecha.isEnabled = false
                    binding.SwitchVisible.isVisible = false
                    filtrarDato(3)
                    binding.btnAplicarFiltros.isVisible = false
                }
            }
        }

        switchIndividual.setOnCheckedChangeListener { view, idChecked ->
            if (idChecked)
                binding.switchCajilla.isChecked = false
            isCajilla = false


        }

        switchCajilla.setOnCheckedChangeListener { view, idChecked ->
            if (idChecked) {
                binding.switchIndividual.isChecked = false
                isCajilla = true
            }
        }

    }


    private fun filtrarDato(numberFilter: Int) {


        when (numberFilter) {
            1 -> {
                initComponent(1)
                val viewModelJob = SupervisorJob()
                val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)


                viewModelScope.launch {
                    val lista = creditoViewModel.obtenerFilterPago()
                    Log.e("BUG", "OBTENIENDO DATOS DE ROOM  $lista")
                }
                creditoViewModel.groupedAmorosoModel.observe(viewLifecycleOwner) { lista ->
                    Log.e("BUG", "AGREGANDO AL ADAPTER  $lista")
                    adapterCredito.setLista(lista)
                }

            }

            2 -> {
                initComponent(2)
                binding.btnAplicarFiltros.setOnClickListener {
                    val fecha = binding.etFecha.text.toString().trim()
                    if (fecha.isEmpty() && !binding.switchCajilla.isChecked && !binding.switchIndividual.isChecked
                    ) {
                        showAlertDialog(
                            "ADVERTENCIA",
                            "No has ingresado una fecha o no has elegido una opcion de ventas"
                        )

                    } else {
                        val viewModelJob = SupervisorJob()
                        val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

                        viewModelScope.launch {
                            if (isCajilla) {
                                try {
                                    ventaViewModel.obtenerFilterCajilla(fecha)
                                    ventaViewModel.ventaModelCajilla.observe(viewLifecycleOwner) { lista ->
                                        adapterVenta.setListCajilla(lista)
                                    }

                                } catch (e: Exception) {
                                    showAlertDialog("Error", "Hubo un error al obtener los datos")
                                }
                            } else {
                                try {
                                    ventaViewModel.obtenerFilterIndividual(fecha)
                                    ventaViewModel.ventaModelIndividual.observe(viewLifecycleOwner) { lista ->
                                        adapterVenta.setListIndividual(lista)
                                    }

                                } catch (e: Exception) {
                                    showAlertDialog("Error", "Hubo un error al obtener los datos")
                                }
                            }
                        }
                    }
                }
            }


            3 -> {
                initComponent(3)
                lifecycleScope.launch { clienteViewModel.obtenerAmoros() }
                clienteViewModel.clienteModel.observe(viewLifecycleOwner) { lista ->
                    adapterCliente.setList(lista)

                }

            }
        }


    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Continuar") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    fun cerrarSesion() {
        val alert = AlertDialog.Builder(requireContext())
            .setTitle("ADVERTENCIA")
            .setMessage("¿Estas seguro que desea cerrar sesion?")
            .setPositiveButton("Aceptar") { dialog, _ ->
                (activity as MainActivity).findViewById<BottomNavigationView>(R.id.NavigationBottom).isVisible =
                    false

                val navController = Navigation.findNavController(binding.root)
                navController.popBackStack()
            }

            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

        alert.show()
    }
}






