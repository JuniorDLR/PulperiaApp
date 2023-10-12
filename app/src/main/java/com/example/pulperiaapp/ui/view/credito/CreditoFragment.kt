package com.example.pulperiaapp.ui.view.credito

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentCreditoBinding

class CreditoFragment : Fragment() {

    private lateinit var binding: FragmentCreditoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreditoBinding.inflate(layoutInflater, container, false)




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAgregarCliente.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_creditoFragment_to_registrarClienteFragment)
            Log.e("BOTON PULSADO", "CLIENTE")
        }

        binding.btnRegistrarMoroso.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_creditoFragment_to_amorosoFragment)
        }
    }

}