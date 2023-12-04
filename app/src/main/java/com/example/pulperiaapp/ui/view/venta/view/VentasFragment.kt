package com.example.pulperiaapp.ui.view.venta.view


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class VentasFragment : Fragment() {

    private val ventasModel: VentaViewModel by viewModels()
    private lateinit var binding: FragmentVentasBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterVenta: AdapterVenta
    private lateinit var tabLayout: TabLayout
    private var esIndividual: Boolean = false


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVentasBinding.inflate(inflater, container, false)
        initComponent()
        binding.root.postDelayed({
            tabLayout.getTabAt(1)?.select()
        }, 100)
        ventasModel.obtenerTotal()

        ventasModel.obtenerTotal.observe(viewLifecycleOwner) { total ->
            val ganancia = "Ganacia: $total"
            binding.tvTotalVenta.text = ganancia
        }




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

        binding.btnAgregarVenta.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.ventasProductoFragment)
        }

        binding.shVentas.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(textoFiltrado: String?): Boolean {
                adapterVenta.filter.filter(textoFiltrado)
                return true
            }

        })

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {

                    val fechaActual = LocalDateTime.now()
                    val zonaHoraria = ZoneId.systemDefault()
                    val fechaInicio = fechaActual.toLocalDate().atStartOfDay(zonaHoraria)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    val fechaFin = fechaActual.toLocalDate().plusDays(1).atStartOfDay(zonaHoraria)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))


                    lifecycleScope.launch {

                        when (it.position) {
                            0 -> {
                                ventasModel.obtenerVentaIndividual(fechaInicio, fechaFin)
                                ventasModel.ventaModelIndividual.collect { lista ->
                                    adapterVenta.setListIndividual(lista)
                                    esIndividual = true
                                }
                            }

                            1 -> {
                                ventasModel.obtenerVentaCajilla(fechaInicio, fechaFin)
                                ventasModel.ventaModelCajilla.collect { lista ->
                                    adapterVenta.setListCajilla(lista)
                                    esIndividual = false
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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initComponent() {
        recyclerView = binding.rvVentas
        tabLayout = binding.tabLayout
        val linear = LinearLayoutManager(requireContext())
        linear.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linear
        adapterVenta =
            AdapterVenta(
                onDeleteClickListener = { idProducto, position, fechaAgrupada ->
                    deleteItem(
                        idProducto,
                        position,
                        fechaAgrupada
                    )
                },
                onUpdateClickListener = { fecha, idProducto -> updateItem(fecha, idProducto) }
            )
        recyclerView.adapter = adapterVenta


    }

    private fun updateItem(fecha: String, idProducto: Int) {

        findNavController().navigate(
            VentasFragmentDirections.actionVentasFragmentToEditarVentasFragment(
                idProducto = idProducto, fechaActual = fecha, esIndividual = esIndividual
            )
        )

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun deleteItem(idProducto: Int, position: Int, fechaAgrupada: String) {
        val alert = AlertDialog.Builder(requireContext())
            .setTitle("ADVERTENCIA")
            .setMessage("¿Desea eliminar esta venta?")
            .setPositiveButton("Si") { dialog, _ ->
                lifecycleScope.launch {

                    if (esIndividual) {
                        ventasModel.eliminarVenta(idProducto)
                    } else {
                        val fechaActual = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                        val ventasCajilla = ventasModel.obtenerVentaCajilla(fechaActual, "")

                        val ventasFiltradas =
                            ventasCajilla.filter { it.fecha_venta == fechaAgrupada }
                        for (fecha in ventasFiltradas) {
                            ventasModel.eliminarVenta(fecha.id)
                            adapterVenta.notifyItemRemoved(position)
                        }
                    }


                }

            }
            .setNegativeButton("no") { diag, _ ->
                diag.dismiss()
            }
        alert.show()
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