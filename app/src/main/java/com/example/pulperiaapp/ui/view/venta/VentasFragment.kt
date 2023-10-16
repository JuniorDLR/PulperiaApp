package com.example.pulperiaapp.ui.view.venta

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentVentasBinding
import com.example.pulperiaapp.ui.view.venta.viewmodel.VentaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VentasFragment : Fragment() {

    private val ventasModel: VentaViewModel by viewModels()
    private lateinit var binding: FragmentVentasBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterVenta: AdapterVenta

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVentasBinding.inflate(inflater, container, false)
        initComponent()
        ventasModel.obtenerVenta()
        ventasModel.ventaModeL.observe(viewLifecycleOwner) { lista ->
            adapterVenta.setList(lista)

        }



        return binding.root
    }


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
    }


    private fun initComponent() {
        recyclerView = binding.rvVentas
        val linear = LinearLayoutManager(requireContext())
        linear.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linear
        adapterVenta = AdapterVenta()
        recyclerView.adapter = adapterVenta


    }
}