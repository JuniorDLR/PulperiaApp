package com.example.pulperiaapp.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentTablaCocaBinding


class TablaCocaFragment : Fragment() {

    private lateinit var binding: FragmentTablaCocaBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTablaCocaBinding.inflate(inflater, container, false)

        binding.btnAgregarProductoCoca.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.agregarCocaFragment)
        }

        return binding.root
    }


}