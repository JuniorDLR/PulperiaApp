package com.example.pulperiaapp.ui.view.prix.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.pulperiaapp.data.database.entitie.PrecioPrixEntity
import com.example.pulperiaapp.databinding.FragmentAgregarPrixBinding
import com.example.pulperiaapp.ui.view.prix.viewmodel.PrixViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AgregarPrixFragment : Fragment() {

    private lateinit var binding: FragmentAgregarPrixBinding
    private val prixModel: PrixViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgregarPrixBinding.inflate(inflater, container, false)

        binding.btnAgregarPrix.setOnClickListener {
            agregarProductoPrix()
        }
        return binding.root
    }


    private fun agregarProductoPrix() {

        val producto = binding.tvProducoAgregarP.text.toString()
        val precio = binding.tvAgregarPrecioP.text.toString()

        if (producto == "" && precio == "") {
            AlertDialog.Builder(requireContext())
                .setTitle("ADVERTENCIA")
                .setMessage("Los campos no pueden quedar vacios")
                .setPositiveButton("Continuar") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else {
            val precioDouble = precio.toDouble()
            val precioPrixEntity = PrecioPrixEntity(0, producto, precioDouble)
            prixModel.insertraPrixTabla(precioPrixEntity)
            requireActivity().supportFragmentManager.popBackStack()
        }

    }

}