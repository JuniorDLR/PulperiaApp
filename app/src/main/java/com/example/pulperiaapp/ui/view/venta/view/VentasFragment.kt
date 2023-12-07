package com.example.pulperiaapp.ui.view.venta.view


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.text.SimpleDateFormat

import java.util.Calendar


@AndroidEntryPoint
class VentasFragment : Fragment() {

    private val ventasModel: VentaViewModel by viewModels()
    private lateinit var binding: FragmentVentasBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterVenta: AdapterVenta
    private lateinit var tabLayout: TabLayout
    private var esIndividual: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVentasBinding.inflate(inflater, container, false)

        initComponent()
        val fechaInicio = obtenerFechaInicioActual()
        val fechaFin = obtenerFechaFin()

        ventasModel.obtenerTotal(fechaInicio, fechaFin)

        binding.root.postDelayed({
            tabLayout.getTabAt(1)?.select()
        }, 100)


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

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val fechaInicio = obtenerFechaInicioActual()
                    val fechaFin = obtenerFechaFin()

                    lifecycleScope.launch {
                        when (it.position) {
                            0 -> {
                                try {

                                    ventasModel.obtenerVentaIndividual(fechaInicio, fechaFin)
                                    ventasModel.ventaModelIndividual.observe(viewLifecycleOwner) { lista ->
                                        adapterVenta.setListIndividual(lista)
                                    }

                                    esIndividual = true
                                    adapterVenta.verificacion("individual")
                                } catch (e: Exception) {
                                    Log.e("TAG", "Error al obtener venta individual: ${e.message}")
                                }
                            }

                            1 -> {
                                try {
                                    ventasModel.obtenerVentaCajilla(fechaInicio, fechaFin)
                                    ventasModel.ventaModelCajilla.observe(viewLifecycleOwner) { lista ->
                                        adapterVenta.setListCajilla(lista)
                                    }
                                    esIndividual = false
                                    adapterVenta.verificacion("Cajilla")
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
                idProducto = idProducto, idFecha = fecha, esIndividual = esIndividual
            )
        )

    }
    @SuppressLint("NotifyDataSetChanged")
    private fun deleteItem(idProducto: Int, position: Int, fechaAgrupada: String) {
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
                                ventasCajilla.filter { it.fecha_venta == fechaAgrupada }
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


    // Función de extensión para obtener la fecha de inicio actual
    private fun obtenerFechaInicioActual(): String {
        val fechaActual = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fechaActual.time)
    }


    private fun obtenerFechaFin(): String {
        val fechaActual = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
        }
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fechaActual.time)

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