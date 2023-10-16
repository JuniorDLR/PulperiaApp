package com.example.pulperiaapp.ui.view.principal

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var binding: FragmentHomeBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        toolbar = binding.root.findViewById(R.id.toolbar)

        toolbar.inflateMenu(R.menu.menu_toolbar)

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.tablaPrix -> {
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.tablaPrixFragment)
                    true
                }

                R.id.tablaCoca -> {
                    Navigation.findNavController(binding.root).navigate(R.id.TablaCocaFragment)
                    true
                }

                R.id.tablaCliente -> {
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.registrarClienteFragment)
                    true
                }


                else -> {

                    false
                }
            }
        }


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            cerrarSesion()
        }
        calback.isEnabled = true
    }

    private fun cerrarSesion() {
        val alert = AlertDialog.Builder(requireContext())
            .setTitle("ADVERTENCIA")
            .setMessage("¿Estas seguro que desea cerrar sesion?")
            .setPositiveButton("Aceptar") { dialog, _ ->
                (activity as MainActivity).findViewById<BottomNavigationView>(R.id.NavigationBottom).isVisible =
                    false
                val navController = Navigation.findNavController(binding.root)
                navController.popBackStack()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

        alert.show()
    }

}



