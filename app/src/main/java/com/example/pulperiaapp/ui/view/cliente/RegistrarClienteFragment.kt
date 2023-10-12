package com.example.pulperiaapp.ui.view.cliente

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentRegistrarClienteBinding
import com.example.pulperiaapp.ui.view.credito.viewmodel.ClienteViewModel

class RegistrarClienteFragment : Fragment() {

    private lateinit var binding: FragmentRegistrarClienteBinding
    private val clienteViewModel: ClienteViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrarClienteBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnAgregarMoroso.setOnClickListener { guardarCliente() }
    }

    private fun guardarCliente() {
        val cliente = binding.tvCliente.text.toString()

    }


}