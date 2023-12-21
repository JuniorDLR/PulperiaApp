package com.example.pulperiaapp.ui.view.coca.view

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.example.pulperiaapp.data.database.entitie.PrecioCocaEntity
import com.example.pulperiaapp.databinding.FragmentAgregarCocaBinding
import com.example.pulperiaapp.ui.view.coca.viewmodel.CocaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AgregarCocaFragment : Fragment() {


    private lateinit var binding: FragmentAgregarCocaBinding
    private val cocaViewModel: CocaViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAgregarCocaBinding.inflate(inflater, container, false)




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAgregarCoca.setOnClickListener { agregarProductoCoca() }


        binding.tvProducoAgregar.doOnTextChanged { text, _, _, _ ->
            if (text!!.length >= 18) {
                binding.textInputLayoutProducto.error = "Limite de caracteres alcanzado"
            } else {
                binding.textInputLayoutProducto.error = null
            }
        }
    }

    private fun agregarProductoCoca() {

        val producto = binding.tvProducoAgregar.text.toString()
        val precio = binding.tvAgregarPrecio.text.toString()

        if (producto.isEmpty() || precio.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("ADVERTENCIA")
                .setMessage("Los campos no pueden quedar vacios")
                .setPositiveButton("Continuar") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else {
            val precioDouble = precio.toDouble()
            val precioCocaEntity = PrecioCocaEntity(0, producto = producto, precio = precioDouble)
            cocaViewModel.insertarCocaTabla(precioCocaEntity)
            requireActivity().supportFragmentManager.popBackStack()
            ocultarTeclado()
            Toast.makeText(requireContext(), "Datos agregado exitosamente!!", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun ocultarTeclado(){
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().window.decorView.windowToken,0)
    }

}