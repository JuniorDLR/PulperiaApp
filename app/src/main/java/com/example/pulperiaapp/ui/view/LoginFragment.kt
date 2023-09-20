package com.example.pulperiaapp.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentLoginBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)


        binding.btnIniciar.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.homeFragment)
            (activity as MainActivity).findViewById<BottomNavigationView>(R.id.NavigationBottom).isVisible =
                true
        }


        return binding.root
    }


}