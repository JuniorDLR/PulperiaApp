package com.example.pulperiaapp.ui.view.principal

import android.annotation.SuppressLint
import android.app.AlertDialog

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.pulperiaapp.R
import com.example.pulperiaapp.data.database.SharedUser
import com.example.pulperiaapp.databinding.FragmentSeguridadBinding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class SeguridadFragment : Fragment() {


    private lateinit var binding: FragmentSeguridadBinding
    private val ACCOUT_SID = "AC46e1a1bbd6f3469febf91b832c3ba345"
    private val AUTH_TOKEN = "716674ce9d5f9820993ba0ec7a01c6d4"
    private val TWILIO_NUMBER = "+16099576794"
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var sharedUser: SharedUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeguridadBinding.inflate(inflater, container, false)


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_seguridadFragment_to_loginFragment)
        }


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedUser = SharedUser(requireContext())

        binding.btnResend.setOnClickListener {
            if (sharedUser.isOneDayPassed()) {
                alertCodigo(
                    "ADVERTENCIA",
                    "Si usted acepta recibir el codigo,no podra recibir otro a menos que halla pasado 24 horas"
                )
            } else {
                alertDialog("Debes esperar un día para poder recibir un código nuevamente")
            }
        }

        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished / 1000
                val opcion = "Enviando codigo en: ${second}s"
                binding.btnResend.text = opcion
                if (second.toInt() == 0) {
                    seguridad()
                }

            }

            override fun onFinish() {
                val estadoNormal = "Tiempo agotado..."
                binding.btnResend.text = estadoNormal


            }

        }

        setupEditextListener()

        binding.btnVerificar.setOnClickListener {
            if (todosEditTextEstanLlenos()) {

                val n1 = binding.otpET1.text.toString()
                val n2 = binding.otpET2.text.toString()
                val n3 = binding.otpET3.text.toString()
                val n4 = binding.otpET4.text.toString()
                val n5 = binding.otpET5.text.toString()
                val codigo = n1 + n2 + n3 + n4 + n5
                val codigoShared = sharedUser.mostrarCodigo()


                if (codigoShared == codigo.toInt()) {
                    sharedUser.eliminarCodigo()
                    findNavController().navigate(
                        SeguridadFragmentDirections.actionSeguridadFragmentToUsuarioFragment(
                            esPw = true
                        )
                    )

                } else {
                    toast("El codigo que usted ingreso no coincide")
                }


            } else {
                alertDialog("El codigo esta en proceso")
            }
        }
    }

    private fun alertCodigo(title: String, messague: String) {
        val alert = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(messague)
            .setPositiveButton("Aceptar") { dialog, _ ->
                startTimer()
                sharedUser.guardarUltimoEnvio()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }

        alert.show()
    }

    private fun setupEditextListener() {


        val editTextList =
            listOf(binding.otpET1, binding.otpET2, binding.otpET3, binding.otpET4, binding.otpET5)

        for ((index, editText) in editTextList.withIndex()) {

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    texto: CharSequence?, start: Int, before: Int, count: Int
                ) {
                    if (before == 1 && index > 0) {
                        editTextList[index - 1].requestFocus()
                    } else {
                        binding.btnVerificar.setBackgroundResource(R.drawable.round_back)
                    }

                }

                override fun onTextChanged(
                    texto: CharSequence?, start: Int, before: Int, count: Int
                ) {

                }

                @SuppressLint("ResourceType")
                override fun afterTextChanged(p0: Editable?) {
                    if (p0?.length == 1) {
                        if (index < editTextList.size - 1) {
                            editTextList[index + 1].requestFocus()
                        } else {
                            if (todosEditTextEstanLlenos()) {
                                binding.btnVerificar.setBackgroundResource(R.drawable.round_red)

                            }
                        }
                    }
                }

            })

        }
    }

    private fun todosEditTextEstanLlenos(): Boolean {
        return binding.otpET1.text.isNotEmpty() &&
                binding.otpET2.text.isNotEmpty() &&
                binding.otpET3.text.isNotEmpty() &&
                binding.otpET4.text.isNotEmpty() &&
                binding.otpET5.text.isNotEmpty()

    }

    private fun seguridad() {
        val client = OkHttpClient()
        val numero = binding.tvPhone.text.toString()
        val codigo = (10000..90000).random()
        val mensaje = "Codigo de verificacion por JunaxerDev: $codigo"

        val mediaType = "application/x-www-form-urlencoded; charset=utf-8".toMediaTypeOrNull()
        val requestBody = "To=$numero&From=$TWILIO_NUMBER&Body=$mensaje".toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.twilio.com/2010-04-01/Accounts/$ACCOUT_SID/Messages.json")
            .post(requestBody).header("Authorization", Credentials.basic(ACCOUT_SID, AUTH_TOKEN))
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            val response = client.newCall(request).execute()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    toast("Codigo enviado")
                    saveCode(codigo)
                } else {
                    toast("Ocurrio un error al enviar el codigo de verificacion ")

                }
            }
        }

    }


    private fun saveCode(codigo: Int) {

        sharedUser.guardarCodigo(codigo)
    }

    private fun startTimer() {
        countDownTimer.start()
    }

    private fun toast(messague: String) {
        Toast.makeText(requireContext(), messague, Toast.LENGTH_LONG).show()
    }

    private fun alertDialog(messague: String) {
        AlertDialog.Builder(requireContext()).setTitle("ERROR DE VERIFICACION ")
            .setMessage(messague).setPositiveButton("Continuar") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}