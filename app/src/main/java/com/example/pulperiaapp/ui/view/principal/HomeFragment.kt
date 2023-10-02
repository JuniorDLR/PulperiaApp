package com.example.pulperiaapp.ui.view.principal

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentHomeBinding

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


                else -> {

                    false
                }
            }
        }


        return binding.root

    }

}



