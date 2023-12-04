package com.example.pulperiaapp.ui.view.cliente

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.pulperiaapp.data.database.entitie.ClienteEntity
import com.example.pulperiaapp.databinding.FragmentRegistrarClienteBinding
import com.example.pulperiaapp.ui.view.credito.viewmodel.ClienteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class RegistrarClienteFragment : Fragment() {

    private lateinit var binding: FragmentRegistrarClienteBinding
    private val clienteViewModel: ClienteViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrarClienteBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnAgregarMoroso.setOnClickListener { guardarCliente() }
    }
    private fun guardarCliente() {
        lifecycleScope.launch {
            val clienteView = binding.tvCliente.text.toString().trim()
            val lista = clienteViewModel.obtenerAmoros()

            if (clienteView.isEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("ADVERTENCIA")
                    .setMessage("Debe ingresar un nombre para continuar")
                    .setPositiveButton("Aceptar") { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            } else {
                val nombreEnMinuscula = clienteView.lowercase(Locale.ROOT)
                val filtrado = lista.any { it.lowercase(Locale.ROOT) == nombreEnMinuscula }

                if (filtrado) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("ADVERTENCIA")
                        .setMessage("Este usuario ya existe en la base de datos")
                        .setPositiveButton("Continuar") { dialog, _ ->
                            dialog.dismiss()
                        }.show()
                } else {
                    lifecycleScope.launch {
                        val entity = ClienteEntity(0, clienteView)
                        clienteViewModel.insertarCliente(entity)
                        binding.tvCliente.setText("")
                        Toast.makeText(
                            requireContext(),
                            "Cliente guardado exitosamente",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }


}


