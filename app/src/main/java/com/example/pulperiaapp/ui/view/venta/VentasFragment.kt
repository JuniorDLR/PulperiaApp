package com.example.pulperiaapp.ui.view.venta



import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.content.ContextCompat

import androidx.fragment.app.viewModels

import androidx.lifecycle.lifecycleScope

import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentVentasBinding
import com.example.pulperiaapp.ui.view.venta.viewmodel.VentaViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class VentasFragment : Fragment() {

    private val ventasModel: VentaViewModel by viewModels()
    private lateinit var binding: FragmentVentasBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterVenta: AdapterVenta


    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVentasBinding.inflate(inflater, container, false)
        initComponent()
        ventasModel.obtenerVenta()
        lifecycleScope.launch {
            ventasModel.ventaModel.collect { lista ->
                adapterVenta.setList(lista)

            }
        }

        ventasModel.obtenerTotal()
        ventasModel.obtenerTotal.observe(viewLifecycleOwner) { total ->
            binding.tvTotalVenta.text = "Ganancia: $$total"
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
        /*
        *
        *   lifecycleScope.launch {
              ventasModel.ventaModelPagin.collect { pagingData ->
                  adapterVenta.submitData(pagingData)
              }
          }
        * */

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
    }


    private fun initComponent() {
        recyclerView = binding.rvVentas
        val linear = LinearLayoutManager(requireContext())
        linear.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linear
        adapterVenta =
            AdapterVenta(
                onDeleteClickListener = { position, idProducto ->
                    deleteItem(
                        position,
                        idProducto
                    )
                },
                onUpdateClickListener = { fecha, idProducto -> updateItem(fecha, idProducto) }
            )
        recyclerView.adapter = adapterVenta


    }

    private fun updateItem(fecha: Long, idProducto: Int) {

        findNavController().navigate(
            VentasFragmentDirections.actionVentasFragmentToEditarVentasFragment(
                idProducto = idProducto, fechaActual = fecha
            )
        )

    }


    private fun deleteItem(position: Int, idProducto: Int) {
        val alert = AlertDialog.Builder(requireContext())
            .setTitle("ADVERTENCIA")
            .setMessage("¿Desea eliminar esta venta?")
            .setPositiveButton("Si") { dialog, _ ->
                lifecycleScope.launch {
                    ventasModel.eliminarVenta(idProducto)
                    adapterVenta.notifyItemRemoved(position)
                }

            }
            .setNegativeButton("no") { diag, _ ->
                diag.dismiss()
            }
        alert.show()
    }


}