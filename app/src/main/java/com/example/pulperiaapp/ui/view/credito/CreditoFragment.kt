package com.example.pulperiaapp.ui.view.credito

import android.os.Bundle
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
    ): View? {
        binding = FragmentCreditoBinding.inflate(layoutInflater)

        binding.btnAgregarCliente.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.amorosoFragment)
        }


        return binding.root
    }


}