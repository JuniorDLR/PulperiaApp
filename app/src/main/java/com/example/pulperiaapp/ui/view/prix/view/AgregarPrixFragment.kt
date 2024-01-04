package com.example.pulperiaapp.ui.view.prix.view

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.pulperiaapp.data.database.entitie.PrecioPrixEntity
import com.example.pulperiaapp.databinding.FragmentAgregarPrixBinding
import com.example.pulperiaapp.ui.view.prix.viewmodel.PrixViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class AgregarPrixFragment : Fragment() {

    private lateinit var binding: FragmentAgregarPrixBinding
    private val prixModel: PrixViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgregarPrixBinding.inflate(inflater, container, false)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAgregarPrix.setOnClickListener {
            agregarProductoPrix()
        }


        binding.tvProducoAgregarP.doOnTextChanged { text, _, _, _ ->
            if (text!!.length >= 18) {
                binding.textInputLayoutProducto.error = "Limite de caracteres alcanzado"
            } else {
                binding.textInputLayoutProducto.error = null
            }
        }
    }

    private fun agregarProductoPrix() {

        val producto = binding.tvProducoAgregarP.text.toString().lowercase(Locale.ROOT).trim()
        val precio = binding.tvAgregarPrecioP.text.toString()

        if (producto.isEmpty() || precio.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("ADVERTENCIA")
                .setMessage("Los campos no pueden quedar vacios")
                .setPositiveButton("Continuar") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else {

            lifecycleScope.launch {
                val productoRecuperado = encontrarProdcuto(producto)
                if (productoRecuperado.isNotEmpty()) {
                    showAlertDialog(
                        "ADVERTENCIA",
                        "Este producto ya esta registrado en la tabla",
                        "Continuar"
                    )

                } else {
                    val precioDouble = precio.toDouble()
                    val precioPrixEntity = PrecioPrixEntity(0, producto, precioDouble)
                    prixModel.insertraPrixTabla(precioPrixEntity)


                    Toast.makeText(
                        requireContext(),
                        "Datos agregado exitosamente!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    val action =
                        AgregarPrixFragmentDirections.actionAgregarPrixFragmentToTablaPrixFragment()
                    Navigation.findNavController(binding.root).navigate(action)
                    ocultarTeclado()
                }
            }
        }

    }

    private suspend fun encontrarProdcuto(producto: String): String {

        return try {
            val lista = prixModel.obtenerPrixTabla()
            for (filtrado in lista) {
                if (filtrado.producto.lowercase(Locale.ROOT).trim() == producto) {
                    return producto
                }
            }
            ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun ocultarTeclado() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().window.decorView.windowToken, 0)


    }

    private fun showAlertDialog(titulo: String, mensaje: String, boton: String) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton(boton) { dialog, _ ->
                dialog.dismiss()
            }
        builder.show()

    }

}