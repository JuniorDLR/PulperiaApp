package com.example.pulperiaapp.ui.view.bigcola.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentTablaBigColaBinding
import com.example.pulperiaapp.domain.bigcola.TablaBig
import com.example.pulperiaapp.ui.view.bigcola.viewmodel.BigColaViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class TablaBigColaFragment : Fragment() {

    private lateinit var binding: FragmentTablaBigColaBinding
    private lateinit var tableLayout: TableLayout
    private val bigColaViewModel: BigColaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTablaBigColaBinding.inflate(inflater, container, false)


        binding.btnAgregarProductoBig.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      lifecycleScope.launch {   bigColaViewModel.obtenerBigcola() }
        tableLayout = binding.tlProductoBig
        bigColaViewModel.bigModdel.observe(viewLifecycleOwner) { lista ->
            mostrarTabla(lista)
        }
        binding.btnAgregarProductoBig.setOnClickListener {

            val action =
                TablaBigColaFragmentDirections.actionTablaBigColaFragmentToAgregandoBigFragment()
            Navigation.findNavController(binding.root).navigate(action)
        }
        binding.btnEditarProductoBig.setOnClickListener { editarBigCola() }
        binding.btnElimimarProductoBig.setOnClickListener { eliminarBigCola() }

        binding.svTablaProductoBig.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                filterTextTable(query)
                return true
            }

        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_tablaBigColaFragment_to_homeFragment)
        }
    }

    private fun filterTextTable(query: String?) {
        val queryText = query?.lowercase(Locale.getDefault())

        for (i in 1 until tableLayout.childCount) {
            val row = tableLayout.getChildAt(i) as TableRow
            var isVisible = false

            for (j in 0 until row.childCount) {
                val textView = row.getChildAt(j) as TextView
                val text = textView.text.toString().lowercase(Locale.getDefault())
                if (text.contains(queryText.orEmpty()))
                    isVisible = true
                break
            }
            row.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }


    private fun mostrarTabla(lista: List<TablaBig>) {
        tableLayout.removeAllViews()


        for (big in lista) {
            val idProdcuto = big.id
            val producto = big.producto
            val precio = big.precio
            actualizarTabla(idProdcuto, producto, precio)

        }


    }

    @SuppressLint("InflateParams")
    private fun actualizarTabla(idProdcuto: Int, producto: String, precio: Double) {

        val tableRow =
            LayoutInflater.from(requireContext())
                .inflate(R.layout.tabla_row_item, null) as TableRow

        val idView = tableRow.findViewById<TextView>(R.id.tvIdR)
        idView.text = idProdcuto.toString()

        val productoView = tableRow.findViewById<TextView>(R.id.tvProductoR)
        productoView.text = producto

        val precioView = tableRow.findViewById<TextView>(R.id.tvPrecioR)
        precioView.text = precio.toString()



        idView.setOnClickListener {
            binding.tvIdEditarBig.setText(idProdcuto.toString())
            binding.tvPrecioEditarBig.setText("")
        }

        productoView.setOnClickListener {
            binding.tvIdEditarBig.setText(idProdcuto.toString())
            binding.tvPrecioEditarBig.setText(precio.toString())
        }


        tableLayout.addView(tableRow)

    }


    private fun editarBigCola() {
        val id = binding.tvIdEditarBig.text.toString()
        val precio = binding.tvPrecioEditarBig.text.toString()

        if (id.isEmpty() || precio.isEmpty()) {
            Snackbar.make(requireView(), "Los campos no pueden quedar vacio", Snackbar.LENGTH_SHORT)
                .show()
        } else if (id.isNotEmpty() && precio.isEmpty() || precio.isNotEmpty() && id.isEmpty()) {
            Snackbar.make(requireView(), "Los campos no pueden quedar vacio", Snackbar.LENGTH_SHORT)
                .show()
        } else {
            val precioDouble = precio.toDouble()
            val idInt = id.toInt()

            lifecycleScope.launch {
                val obtenerIdPrecio = bigColaViewModel.obtenerPrecioId(idInt)
                if (precioDouble.equals(obtenerIdPrecio)) {
                    Snackbar.make(requireView(), "El precio es el mismo", Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    bigColaViewModel.editarBigCola(precioDouble, idInt)
                    limpiarCampos()
                }


            }
        }
    }

    private fun limpiarCampos() {
        binding.tvIdEditarBig.setText("")
        binding.tvPrecioEditarBig.setText("")
        Toast.makeText(requireContext(), "Datos editado exitosamente!!", Toast.LENGTH_SHORT)
            .show()
    }

    private fun eliminarBigCola() {
        val idEliminar = binding.tvIdEditarBig.text.toString()
        val precioEliminar = binding.tvPrecioEditarBig.text.toString()

        if (idEliminar.isEmpty()) {

            Snackbar.make(requireView(), "El campo id esta vacio", Snackbar.LENGTH_SHORT).show()

        } else if (precioEliminar.isNotEmpty() || idEliminar.isEmpty()) {

            Snackbar.make(
                requireView(),
                "Solo debes introducir el id para eliminar",
                Snackbar.LENGTH_LONG
            )
                .show()
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("ALERTA PRODUCTO")
                .setMessage("¿Estas seguro que deseas eliminarlo?")
                .setPositiveButton("Aceptar") { dialog, _ ->
                    val idInt = idEliminar.toInt()
                    bigColaViewModel.eliminarBigCola(idInt)
                    binding.tvPrecioEditarBig.setText("")
                    binding.tvIdEditarBig.setText("")
                    Toast.makeText(
                        requireContext(),
                        "Datos eliminado exitosamente!!",
                        Toast.LENGTH_SHORT
                    ).show()

                    dialog.dismiss()

                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }


    }

}