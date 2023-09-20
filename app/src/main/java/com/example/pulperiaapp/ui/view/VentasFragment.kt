package com.example.pulperiaapp.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentVentasBinding


class VentasFragment : Fragment() {
    private lateinit var binding: FragmentVentasBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVentasBinding.inflate(inflater, container, false)


        binding.btnAgregarVenta.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.ventasProductoFragment)
        }

        return binding.root
    }


}