package com.example.pulperiaapp.ui.view.coca

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.pulperiaapp.R
import com.example.pulperiaapp.data.database.entitie.coca.PrecioCocaEntity
import com.example.pulperiaapp.databinding.FragmentAgregarCocaBinding
import com.example.pulperiaapp.domain.coca.TablaCoca
import com.example.pulperiaapp.ui.view.coca.viewmodel.CocaViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AgregarCocaFragment : Fragment() {


    private lateinit var binding: FragmentAgregarCocaBinding
    private val cocaViewModel: CocaViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAgregarCocaBinding.inflate(inflater, container, false)




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAgregarCoca.setOnClickListener { agregarProductoCoca() }
    }

    private fun agregarProductoCoca() {

        val producto = binding.tvProducoAgregar.text.toString()
        val precio = binding.tvAgregarPrecio.text.toString()

        if (producto == "" && precio == "") {
            AlertDialog.Builder(requireContext())
                .setTitle("ADVERTENCIA")
                .setMessage("Los campos no pueden quedar vaciones")
                .setPositiveButton("Continuar") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else {
            val precioDouble = precio.toDouble()
            val precioCocaEntity = PrecioCocaEntity(0, producto = producto, precio = precioDouble)
            cocaViewModel.insertarCocaTabla(precioCocaEntity)
            requireActivity().supportFragmentManager.popBackStack()
        }

    }

}