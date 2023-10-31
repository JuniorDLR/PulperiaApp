package com.example.pulperiaapp.ui.view.bigcola

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.pulperiaapp.R
import com.example.pulperiaapp.data.database.entitie.PrecioBigCola
import com.example.pulperiaapp.databinding.FragmentAgregandoBigBinding
import com.example.pulperiaapp.ui.view.bigcola.viewmodel.BigColaViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AgregandoBigFragment : Fragment() {

    private lateinit var binding: FragmentAgregandoBigBinding
    private val viewModelBig: BigColaViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgregandoBigBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAgregarBig.setOnClickListener { agregarBigCola() }


    }

    private fun agregarBigCola() {
        val producto = binding.tvProducoAgregar.text.toString()
        val precio = binding.tvAgregarPrecio.text.toString()

        if (producto.isEmpty() || precio.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("ADVERTENCIA")
                .setMessage("Los campos no pueden quedar vaciones")
                .setPositiveButton("Continuar") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else {
            val precioDouble = precio.toDouble()
            val entitie = PrecioBigCola(0, producto, precioDouble)
            viewModelBig.insertarBigCola(entitie)
            Toast.makeText(requireContext(), "Datos guardado exitosamente", Toast.LENGTH_LONG)
                .show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

}