package com.example.pulperiaapp.ui.view.bigcola.view

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
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
        binding.tvProducoAgregar.doOnTextChanged { text, _, _, _ ->
            if (text!!.length >= 18) {
                binding.textInputLayoutProducto.error = "Limite de caracteres alcanzado"
            } else if (text.length < 18) {
                binding.textInputLayoutProducto.error = null
            }
        }


    }

    private fun agregarBigCola() {
        val producto = binding.tvProducoAgregar.text.toString()
        val precio = binding.tvAgregarPrecio.text.toString()

        if (producto.isEmpty() || precio.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("ADVERTENCIA")
                .setMessage("Los campos no pueden quedar vacios")
                .setPositiveButton("Continuar") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else {
            val precioDouble = precio.toDouble()
            val entitie = PrecioBigCola(0, producto, precioDouble)
            viewModelBig.insertarBigCola(entitie)
            Toast.makeText(requireContext(), "Datos guardado exitosamente", Toast.LENGTH_LONG)
                .show()
            ocultarTeclado()
            val action =
                AgregandoBigFragmentDirections.actionAgregandoBigFragmentToTablaBigColaFragment()
            Navigation.findNavController(binding.root).navigate(action)

        }
    }

    private fun ocultarTeclado() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().window.decorView.windowToken, 0)


    }

}