package com.example.pulperiaapp.ui.view.prix

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Typeface
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentTablaPrixBinding
import com.example.pulperiaapp.domain.prix.TablaPrix
import com.example.pulperiaapp.ui.view.prix.viewmodel.PrixViewModel
import dagger.hilt.android.AndroidEntryPoint
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
    ): View? {
        binding = FragmentTablaPrixBinding.inflate(inflater, container, false)
        binding.btnAgregarProductoPrix.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        prixViewModel.obtenerPrixTabla()
        prixViewModel.prixModel.observe(viewLifecycleOwner) { lists ->
            mostrarTabla(lists)
        }



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAgregarProductoPrix.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.agregarPrixFragment)
        }

        binding.btnEditarProductoPrix.setOnClickListener {
            editarProducto()

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

    fun limpiarCampos() {
        binding.tvIdEditarPrix.setText("")
        binding.tvPrecioEditarPrix.setText("")

    }

    private fun editarProducto() {
        val idEditar = binding.tvIdEditarPrix.text.toString()
        val precioEditar = binding.tvPrecioEditarPrix.text.toString()

        if (idEditar == "" && precioEditar == "") {
            Toast.makeText(requireContext(), "Los campos estan vacios", Toast.LENGTH_LONG)
                .show()

        } else {
            val idInt = idEditar.toInt()
            val precioInt = precioEditar.toDouble()
            prixViewModel.editarPrixTabla(idInt, precioInt)
            limpiarCampos()
        }
    }


    @SuppressLint("SetTextI18n", "InflateParams")
    private fun mostrarTabla(lists: List<TablaPrix>) {

        tableLayout.removeAllViews()
        for (row in lists) {
            val tableRow = LayoutInflater.from(requireContext())
                .inflate(R.layout.tabla_row_item, null) as TableRow

            val id = tableRow.findViewById<TextView>(R.id.tvIdR)
            id.text = row.id.toString()

            val producto = tableRow.findViewById<TextView>(R.id.tvProductoR)
            producto.text = row.producto


            val precio = tableRow.findViewById<TextView>(R.id.tvPrecioR)
            precio.text = row.precio.toString()


            tableLayout.addView(tableRow)
        }
    }

    private fun eliminarProducto() {

        val idEliminar = binding.tvIdEditarPrix.text.toString()
        val precioEliminar = binding.tvPrecioEditarPrix.text.toString()


        if (idEliminar.isEmpty()) {
            Toast.makeText(requireContext(), "El campo id esta vacio ", Toast.LENGTH_LONG).show()

        } else if (precioEliminar.isNotEmpty()) {
            Toast.makeText(
                requireContext(),
                "Para eliminar solo se debe introducir el id ",
                Toast.LENGTH_LONG
            ).show()


        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("ALERTA PRODUCTO")
                .setMessage("¿Estas seguro que deseas eliminarlo?")
                .setPositiveButton("Aceptar") { dialog, _ ->
                    val id = binding.tvIdEditarPrix.text.toString().toInt()
                    prixViewModel.eliminarPrixTabla(id)
                    Toast.makeText(
                        requireContext(),
                        "Dato eliminado exitosamente!!",
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