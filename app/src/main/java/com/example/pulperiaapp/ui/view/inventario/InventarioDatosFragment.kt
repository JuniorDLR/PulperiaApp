package com.example.pulperiaapp.ui.view.inventario

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentInventarioDatosBinding
import com.example.pulperiaapp.ui.view.inventario.adapter.InventarioAdapter
import com.example.pulperiaapp.ui.view.inventario.viewmodel.InventarioViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InventarioDatosFragment : Fragment() {

    private lateinit var binding: FragmentInventarioDatosBinding
    private val inventarioModel: InventarioViewModel by viewModels()
    private lateinit var adapter: InventarioAdapter
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentInventarioDatosBinding.inflate(inflater, container, false)


        inventarioModel.obtenerInventario()
        inventarioModel.inventarioModel.observe(viewLifecycleOwner) { lista ->
            adapter.setList(lista)
        }
        binding.btnAgregarInventario.setColorFilter(
            ContextCompat.getColor(
                requireContext(), R.color.white
            )
        )



        return binding.root
    }

    private fun initComponent() {
        recyclerView = binding.rvInventario
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager

        adapter = InventarioAdapter(onClickDelete = { position, id -> eliminarItem(position, id) })
        recyclerView.adapter = adapter

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun eliminarItem(position: Int, id: Int) {
        adapter.notifyItemRemoved(position)
        inventarioModel.eliminarInventario(id)
        adapter.notifyDataSetChanged()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponent()
        binding.btnAgregarInventario.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.inventarioFragment)
        }
    }


}