package com.example.pulperiaapp.ui.view.principal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeguridadBinding.inflate(inflater, container, false)

        binding.btnEnviarCodigo.setOnClickListener {
            seguridad()
        }


        return binding.root
    }

    private fun seguridad() {
        val client = OkHttpClient()
        val numero = binding.tvNumero.text.toString()
        val codigo = (10000..90000).random()
        val mensaje = "Codigo de verificacion por JunaxerDev:$codigo"

        val mediaType = "application/x-www-form-urlencoded; charset=utf-8".toMediaTypeOrNull()
        val requestBody = "To=$numero&From=$TWILIO_NUMBER&Body=$mensaje".toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.twilio.com/2010-04-01/Accounts/$ACCOUT_SID/Messages.json")
            .post(requestBody)
            .header("Authorization", Credentials.basic(ACCOUT_SID, AUTH_TOKEN))
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            val response = client.newCall(request).execute()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Codigo enviado", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

}