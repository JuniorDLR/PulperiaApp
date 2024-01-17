package com.example.pulperiaapp.ui.view.prix.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentTablaPrixBinding
import com.example.pulperiaapp.domain.prix.TablaPrix
import com.example.pulperiaapp.ui.view.prix.viewmodel.PrixViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@AndroidEntryPoint
class TablaPrixFragment : Fragment() {

    private lateinit var binding: FragmentTablaPrixBinding
    private val prixViewModel: PrixViewModel by viewModels()

    private lateinit var searchView: SearchView
    private lateinit var tableLayout: TableLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTablaPrixBinding.inflate(inflater, container, false)
        binding.btnAgregarProductoPrix.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )

        prixViewModel.prixModel.observe(viewLifecycleOwner) { lists ->
            mostrarTabla(lists)
        }



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch { prixViewModel.obtenerPrixTabla() }
        binding.btnAgregarProductoPrix.setOnClickListener {
            val action = TablaPrixFragmentDirections.actionTablaPrixFragmentToAgregarPrixFragment()
            findNavController().navigate(action)

        }

        binding.btnEditarProductoPrix.setOnClickListener {
            editarProducto()

        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_tablaPrixFragment_to_homeFragment)
        }
        binding.btnEliminarProductoPrix.setOnClickListener {
            eliminarProducto()
        }
        tableLayout = binding.tlProducto
        searchView = binding.svTablaProductoPrix

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterTableQuery(query)
                return true

            }

            override fun onQueryTextChange(queryText: String?): Boolean {
                filterTableQuery(queryText)
                return true
            }

        })


    }

    private fun filterTableQuery(query: String?) {

        val queryText = query?.lowercase(Locale.getDefault())


        for (i in 1 until tableLayout.childCount) {
            val row = tableLayout.getChildAt(i) as TableRow
            var isVisible = false

            for (j in 0 until row.childCount) {
                val textView = row.getChildAt(j) as TextView
                val texto = textView.text.toString().lowercase(Locale.getDefault())

                if (texto.contains(queryText.orEmpty())) {


                    isVisible = true
                    break

                }
            }

            row.visibility = if (isVisible) View.VISIBLE else View.GONE
        }

    }

    private fun limpiarCampos() {
        binding.tvIdEditarPrix.setText("")
        binding.tvPrecioEditarPrix.setText("")
        Toast.makeText(requireContext(), "Datos editado exitosamente!!", Toast.LENGTH_SHORT)
            .show()

    }

    private fun editarProducto() {
        val idEditar = binding.tvIdEditarPrix.text.toString()
        val precioEditar = binding.tvPrecioEditarPrix.text.toString()

        if (idEditar.isEmpty() || precioEditar.isEmpty()) {
            Snackbar.make(requireView(), "Los campos no pueden quedar vacio", Snackbar.LENGTH_SHORT)
                .show()


        } else if (idEditar.isNotEmpty() && precioEditar.isEmpty() || precioEditar.isNotEmpty() && idEditar.isEmpty()) {
            Snackbar.make(requireView(), "Los campos no pueden quedar vacio", Snackbar.LENGTH_SHORT)
                .show()

        } else {
            val idInt = idEditar.toInt()
            val precioDouble = precioEditar.toDouble()
            lifecycleScope.launch {
                val obtenerPrecioId = prixViewModel.obtenerPrecioId(idInt)

                if (precioDouble.equals(obtenerPrecioId)) {
                    Snackbar.make(requireView(), "El precio es el mismo", Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    prixViewModel.editarPrixTabla(idInt, precioDouble)
                    limpiarCampos()

                }


            }
        }
    }


    @SuppressLint("SetTextI18n", "InflateParams")
    private fun mostrarTabla(lists: List<TablaPrix>) {

        tableLayout.removeAllViews()
        for (row in lists) {
            val idProducto = row.id
            val producto = row.producto
            val precio = row.precio
            actualizarTabla(idProducto, producto, precio)

        }
    }


    @SuppressLint("InflateParams")
    private fun actualizarTabla(idProducto: Int, producto: String, precio: Double) {
        val tableRow = LayoutInflater.from(requireContext())
            .inflate(R.layout.tabla_row_item, null) as TableRow

        val idView = tableRow.findViewById<TextView>(R.id.tvIdR)
        idView.text = idProducto.toString()

        val productoView = tableRow.findViewById<TextView>(R.id.tvProductoR)
        productoView.text = producto

        val precioFormateado = formatearPrecio(precio)
        val precioView = tableRow.findViewById<TextView>(R.id.tvPrecioR)
        precioView.text = precioFormateado



        idView.setOnClickListener {
            binding.tvIdEditarPrix.setText(idProducto.toString())
            binding.tvPrecioEditarPrix.setText("")
        }


        productoView.setOnClickListener {
            binding.tvIdEditarPrix.setText(idProducto.toString())
            binding.tvPrecioEditarPrix.setText(precio.toString())
        }


        tableLayout.addView(tableRow)
    }

    private fun formatearPrecio(precio: Double): String? {
        val bigDecimal = BigDecimal.valueOf(precio)
        val format = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.getDefault()))
        return format.format(bigDecimal)
    }

    private fun eliminarProducto() {

        val idEliminar = binding.tvIdEditarPrix.text.toString()
        val precioEliminar = binding.tvPrecioEditarPrix.text.toString()


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
                    val id = binding.tvIdEditarPrix.text.toString().toInt()
                    prixViewModel.eliminarPrixTabla(id)
                    Toast.makeText(
                        requireContext(),
                        "Datos eliminado exitosamente!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.tvIdEditarPrix.setText("")
                    dialog.dismiss()

                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()


        }


    }

}