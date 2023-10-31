package com.example.pulperiaapp.ui.view.inventario

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentDatosInventarioBinding


class DatosInventarioFragment : Fragment() {

    private lateinit var binding: FragmentDatosInventarioBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDatosInventarioBinding.inflate(inflater, container, false)

        binding.btnAgregarInventario.setColorFilter(
            ContextCompat.getColor(
                requireContext(), R.color.white
            )
        )



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAgregarInventario.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.inventarioFragment)
        }
    }

}