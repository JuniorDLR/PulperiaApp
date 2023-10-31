package com.example.pulperiaapp.ui.view.principal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentLoginBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val user = "Admin"
    private val pw = "1234"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnIniciar.setOnClickListener {
            val usuario = binding.tvUser.text.toString()
            val password = binding.tvPw.text.toString()
            if (usuario.contains(user) && password.contains(pw)) {
                Navigation.findNavController(binding.root).navigate(R.id.homeFragment)
                (activity as MainActivity).findViewById<BottomNavigationView>(R.id.NavigationBottom).isVisible =
                    true
                binding.tvUser.setText("")
                binding.tvPw.setText("")

            } else {
                Toast.makeText(requireContext(), "Credenciales incorrectas", Toast.LENGTH_LONG)
                    .show()
            }


        }
        binding.tvRestablecer.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.seguridadFragment)
        }


    }

}