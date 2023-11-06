package com.example.pulperiaapp.ui.view.principal

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnIniciar.setOnClickListener {

            Navigation.findNavController(binding.root).navigate(R.id.homeFragment)
            (activity as MainActivity).findViewById<BottomNavigationView>(R.id.NavigationBottom).isVisible =
                true
            binding.tvUser.setText("")
            binding.tvPw.setText("")
            ocultarTeclado()
      /*
      *       val usuario = binding.tvUser.text.toString()
            val password = binding.tvPw.text.toString()
            if (usuario.contains(user) && password.contains(pw)) {
                Navigation.findNavController(binding.root).navigate(R.id.homeFragment)
                (activity as MainActivity).findViewById<BottomNavigationView>(R.id.NavigationBottom).isVisible =
                    true
                binding.tvUser.setText("")
                binding.tvPw.setText("")
                ocultarTeclado()

            } else {
                Toast.makeText(requireContext(), "Credenciales incorrectas", Toast.LENGTH_LONG)
                    .show()
            }
      *
      *
      * */


        }
        binding.tvRestablecer.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.seguridadFragment)
        }

    }
    fun ocultarTeclado() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().window.decorView.windowToken, 0)
    }

}