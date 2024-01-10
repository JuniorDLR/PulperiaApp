package com.example.pulperiaapp.ui.view.usuario

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pulperiaapp.data.database.entitie.UsuarioEntity
import com.example.pulperiaapp.databinding.FragmentUsuarioBinding
import com.example.pulperiaapp.domain.usuario.UsuarioViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale


@AndroidEntryPoint
class UsuarioFragment : Fragment() {

    private lateinit var binding: FragmentUsuarioBinding
    private val usuarioViewModel: UsuarioViewModel by viewModels()
    private var todoBien = true
    val navArgs: UsuarioFragmentArgs by navArgs()
    var esUsuario: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsuarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val esP = navArgs.esPw
        val changeMode = configuracion()
        val changeBtn = configuracionBtn()

        changeMode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(password: Editable?) {
                password?.let {
                    verificarRequerimiento(password)
                }


            }

        })

        if (esP) {
            with(binding) {
                toolbarUsuario.title = "Restableciendo contraseña"
                llUsario.isVisible = false
                llRestablecerPw.isVisible = true


            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val action = UsuarioFragmentDirections.actionUsuarioFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        changeBtn.setOnClickListener {
            if (navArgs.esPw) restablecer() else usuario()
        }
    }

    private fun restablecer() {
        lifecycleScope.launch {
            val pw1 = binding.tvPw1.text.toString()
            val pw2 = binding.tvPw2.text.toString()

            if (todoBien) {

                if (pw1 == pw2) {
                    val verificacion = listaUsuario(pw1)
                    if (!verificacion) {
                        val action =
                            UsuarioFragmentDirections.actionUsuarioFragmentToLoginFragment()
                        findNavController().navigate(action)
                        usuarioViewModel.editarUsuario(pw1, 1)
                        Toast.makeText(requireContext(), "Contraseña cambiada", Toast.LENGTH_LONG)
                            .show()

                    } else {

                        showAlertDialog(
                            "Error",
                            "La contraseña que usted ingreso es la misma que la anterior"
                        )
                    }


                } else {
                    showAlertDialog(
                        "Error",
                        "La contraseña que usted ingreso no son iguales"
                    )
                }

            } else {
                showAlertDialog(
                    "Error",
                    "No cumples con los requisitos para agregar un nuevo usuario"
                )
            }
        }
    }

    private suspend fun listaUsuario(pw1: String): Boolean {

        val lista = usuarioViewModel.obtenerUsuario()
        for (verificar in lista) {
            if (verificar.pw == pw1) {
                return true
            }
        }
        return false
    }

    private fun usuario() {
        val usuario = binding.tvUsuario.text.toString()
        val pw = binding.tvPw.text.toString()
        if (todoBien) {
            lifecycleScope.launch {
                val resgistro = usuarioViewModel.obtenerUsuario().any {
                    it.usuario.lowercase(Locale.ROOT)
                        .contains(usuario.lowercase(Locale.getDefault())) && it.pw.lowercase(
                        Locale.ROOT
                    ).contains(pw.lowercase(Locale.getDefault()))
                }

                if (resgistro) {
                    showAlertDialog(
                        "Error",
                        "Las credenciales que usted ingreso ya estan registradas en la base de datos"
                    )

                } else {
                    val userEntity = UsuarioEntity(usuario = usuario, pw = pw)
                    usuarioViewModel.agregarUsuario(userEntity)
                    Toast.makeText(
                        requireContext(),
                        "Usuario registrado con exito!",
                        Toast.LENGTH_LONG
                    ).show()
                    requireActivity().supportFragmentManager.popBackStack()
                }

            }
        } else {
            showAlertDialog(
                "Error",
                "No cumples con los requisitos para agregar un nuevo usuario"
            )


        }
    }

    private fun configuracionBtn(): Button {
        return if (navArgs.esPw) binding.btnRestablecerPw else binding.btnRegistar
    }

    private fun configuracion(): TextInputEditText {
        return if (navArgs.esPw) binding.tvPw1 else binding.tvPw
    }


    private fun verificarRequerimiento(password: Editable) {
        val requerimientos = listOf(
            "Requisitos de Contraseña:",
            "Al menos 8 caracteres de longitud.",
            "Al menos una letra mayúscula.",
            "Al menos un número.",
            "Puede contener caracteres especiales como @, #, $, %."
        )

        requerimientos.joinToString("\n")


        var warningText = ""
        todoBien = true

        warningText += "<font color='#000000'>" + requerimientos[0] + "</font><br>"

        if (password.length < 8) {
            warningText += "<font color='#AD3E3E'>" + requerimientos[1] + "</font><br>"
            todoBien = false
        } else {
            warningText += "<font color='#275457'>" + requerimientos[1] + "</font><br>"

        }

        if (!password.any { it.isUpperCase() }) {
            warningText += "<font color='#AD3E3E'>" + requerimientos[2] + "</font><br>"
            todoBien = false
        } else {
            warningText += "<font color='#275457'>" + requerimientos[2] + "</font><br>"

        }

        if (!password.any { it.isDigit() }) {
            warningText += "<font color='#AD3E3E'>" + requerimientos[3] + "</font><br>"
            todoBien = false
        } else {
            warningText += "<font color='#275457'>" + requerimientos[3] + "</font><br>"

        }

        if (!password.any { it in setOf('@', '#', '$', '%') }) {
            warningText += "<font color='#AD3E3E'>" + requerimientos[4] + "</font>"
            todoBien = false
        } else {
            warningText += "<font color='#275457'>" + requerimientos[4] + "</font>"

        }


        binding.tvPasswordRequirements.text = Html.fromHtml(warningText, Html.FROM_HTML_MODE_LEGACY)
    }


    private fun showAlertDialog(titulo: String, msm: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(titulo)
            .setMessage(msm)
            .setPositiveButton("Continuar") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}