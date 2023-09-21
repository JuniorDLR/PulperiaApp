package com.example.pulperiaapp.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentTablaPrixBinding


class TablaPrixFragment : Fragment() {

    private lateinit var binding: FragmentTablaPrixBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTablaPrixBinding.inflate(inflater, container, false)

        binding.btnAgregarProductoPrix.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.agregarPrixFragment)
        }

        return binding.root
    }


}