package com.example.pulperiaapp.ui.view.principal

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
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
import androidx.navigation.fragment.findNavController
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentLoginBinding
import com.example.pulperiaapp.domain.usuario.UsuarioViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginFragment : Fragment() {


    private lateinit var binding: FragmentLoginBinding
    private lateinit var biometricPrompt: BiometricPrompt
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

        biometricPrompt = createBiometricPrompt()
        verificarUsuario()
        binding.btnIniciar.setOnClickListener {
            iniciarSesion()
        }
        binding.tvRestablecer.setOnClickListener {
            if (!resgistroCheck) {
                val action =
                    LoginFragmentDirections.actionLoginFragmentToUsuarioFragment(esPw = false)
                findNavController().navigate(action)

            } else {
                val action = LoginFragmentDirections.actionLoginFragmentToSeguridadFragment()
                findNavController().navigate(action)
            }
        }
        binding.btnHuella.setOnClickListener {
            iniciarHuella()
        }

    }

    private fun iniciarHuella() {
        showBiometricPrompr()
    }


    private fun createBiometricPrompt(): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS) {
                    // No hay huellas dactilares configuradas
                    showToast("No hay huellas dactilares configuradas en el dispositivo.")
                } else {
                    // Otro tipo de error de autenticación
                    showToast("Error de autenticación: $errString")
                }

            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                // La autenticación ha tenido éxito
                showToast("Autenticación exitosa")
                navegarHome()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // La autenticación ha fallado
                showToast("Autenticación fallida")
            }


        }

        return BiometricPrompt(requireActivity(), executor, callback)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showBiometricPrompr() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticacion de huella")
            .setSubtitle("Usa tu huella dactilar para acceder")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun verificarUsuario() {
        lifecycleScope.launch {
            val listaUsuarios = usuarioViewModel.obtenerUsuario()
            resgistroCheck = listaUsuarios.isNotEmpty()
            if (resgistroCheck) {
                binding.tvRestablecer.isVisible = true
            } else {
                val cuenta = "Crear cuenta de usuario"
                binding.tvRestablecer.text = cuenta

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

                showToast("Debes de ingresar tus credenciales")

            } else if ((usuario == usuarioRoom && contrasena == contrasenaRoom) || (usuario == user && contrasena == pw)) {

                navegarHome()
                ocultarTeclado()
            } else {
                showToast("Credenciales incorrectas")
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

    private fun navegarHome() {
        Navigation.findNavController(binding.root).navigate(R.id.homeFragment)
        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.NavigationBottom).isVisible =
            true
        binding.tvUser.setText("")
        binding.tvPw.setText("")
    }

}