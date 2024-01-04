package com.example.pulperiaapp.ui.view.coca.view

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
import com.example.pulperiaapp.data.database.entitie.PrecioCocaEntity
import com.example.pulperiaapp.databinding.FragmentAgregarCocaBinding
import com.example.pulperiaapp.ui.view.coca.viewmodel.CocaViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class AgregarCocaFragment : Fragment() {


    private lateinit var binding: FragmentAgregarCocaBinding
    private val cocaViewModel: CocaViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAgregarCocaBinding.inflate(inflater, container, false)




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAgregarCoca.setOnClickListener { agregarProductoCoca() }


        binding.tvProducoAgregar.doOnTextChanged { text, _, _, _ ->
            if (text!!.length >= 18) {
                binding.textInputLayoutProducto.error = "Limite de caracteres alcanzado"
            } else {
                binding.textInputLayoutProducto.error = null
            }
        }
    }


    private fun agregarProductoCoca() {

        val producto = binding.tvProducoAgregar.text.toString().lowercase(Locale.ROOT).trim()
        val precio = binding.tvAgregarPrecio.text.toString()

        if (producto.isEmpty() || precio.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("ADVERTENCIA")
                .setMessage("Los campos no pueden quedar vacios")
                .setPositiveButton("Continuar") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else {

            lifecycleScope.launch {
                val precioDouble = precio.toDouble()
                val productoLista = encontrarProdcuto(producto)
                if (productoLista.isNotEmpty()) {
                    showAlertDialog(
                        "ADVERTENCIA",
                        "Este producto ya esta registrado en la tabla",
                        "Continuar"
                    )

                } else {
                    val precioCocaEntity =
                        PrecioCocaEntity(0, producto = producto, precio = precioDouble)
                    cocaViewModel.insertarCocaTabla(precioCocaEntity)

                    Toast.makeText(
                        requireContext(),
                        "Datos agregado exitosamente!!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    ocultarTeclado()
                    val action =
                        AgregarCocaFragmentDirections.actionAgregarCocaFragmentToTablaCocaFragment()
                    Navigation.findNavController(binding.root).navigate(action)
                }
            }
        }

    }

    private suspend fun encontrarProdcuto(producto: String): String {
        return try {
            val lista = cocaViewModel.obtenerCocaTabla()
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

    private fun showAlertDialog(titulo: String, mensaje: String, boton: String) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton(boton) { dialog, _ ->
                dialog.dismiss()
            }
        builder.show()

    }

    private fun ocultarTeclado() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().window.decorView.windowToken, 0)


    }

}