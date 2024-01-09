package com.example.pulperiaapp.ui.view.principal

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentLoginBinding
import com.example.pulperiaapp.domain.usuario.UsuarioViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginFragment : Fragment() {


    private lateinit var binding: FragmentLoginBinding
    private val user = "Admin"
    private val pw = "qwerty123"
    private val usuarioViewModel: UsuarioViewModel by viewModels()
    private var resgistroCheck: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        verificarUsuario()
        binding.btnIniciar.setOnClickListener {
            iniciarSesion()
        }
        binding.tvRestablecer.setOnClickListener {
            if (!resgistroCheck) {
                Navigation.findNavController(binding.root).navigate(R.id.usuarioFragment)
            }else{
                Navigation.findNavController(binding.root).navigate(R.id.seguridadFragment)
            }
        }


    }

    private fun verificarUsuario() {
        lifecycleScope.launch {
            val listaUsuarios = usuarioViewModel.obtenerUsuario()
            resgistroCheck = listaUsuarios.isNotEmpty()
            if (resgistroCheck) {
                binding.tvRestablecer.isVisible = true
            }else{
                binding.tvRestablecer.text = "Crear cuenta de usuario"

            }
        }
    }

    private fun iniciarSesion() {
        lifecycleScope.launch {
            val usuario = binding.tvUser.text.toString()
            val contrasena = binding.tvPw.text.toString()
            val usuarioRoom = obtenerUsuario()
            val contrasenaRoom = obtenerContrasena()

            if (usuario.isEmpty() && contrasena.isEmpty()) {

                Toast.makeText(
                    requireContext(),
                    "Debes de ingresar tus credenciales ",
                    Toast.LENGTH_LONG
                ).show()

            } else if ((usuario == usuarioRoom && contrasena == contrasenaRoom) || (usuario == user && contrasena == pw)) {
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
        }
    }

    private suspend fun obtenerContrasena(): String {
        val listaUsuarios = usuarioViewModel.obtenerUsuario()
        return listaUsuarios.firstOrNull()?.pw.orEmpty()
    }

    private suspend fun obtenerUsuario(): String {
        val listaUsuarios = usuarioViewModel.obtenerUsuario()
        return listaUsuarios.firstOrNull()?.usuario.orEmpty()
    }


    private fun ocultarTeclado() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().window.decorView.windowToken, 0)
    }

}