package com.example.pulperiaapp.ui.view.coca

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Typeface
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentTablaCocaBinding
import com.example.pulperiaapp.domain.coca.TablaCoca
import com.example.pulperiaapp.ui.view.coca.viewmodel.CocaViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class TablaCocaFragment : Fragment() {

    private lateinit var binding: FragmentTablaCocaBinding
    private val cocaViewModel: CocaViewModel by viewModels()
    private lateinit var searchView: SearchView
    private lateinit var tableLayout: TableLayout


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTablaCocaBinding.inflate(inflater, container, false)
        cocaViewModel.obtenerCocaTabla()
        binding.btnAgregarProductoCoca.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        cocaViewModel.cocaViewModel.observe(viewLifecycleOwner) { lists ->
            mostrarTabla(lists)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAgregarProductoCoca.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_TablaCocaFragment_to_agregarCocaFragment)
        }
        binding.btnEditarProductoCoca.setOnClickListener {
            editarProducto()
        }
        binding.btnElimimarProductoCoca.setOnClickListener {
            eliminarProducto()
        }

        searchView = binding.svTablaProductoCoca
        tableLayout = binding.tlProducto

        binding.svTablaProductoCoca.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                //Cuando el presiona el buscar
                filterTableQuery(query)
                return true
            }

            override fun onQueryTextChange(nextText: String?): Boolean {
                //Cuando se esta modificando la busqueda
                filterTableQuery(nextText)
                return true
            }

        })

    }

    private fun filterTableQuery(query: String?) {

        val queryTex = query?.lowercase(Locale.getDefault())

        for (i in 1 until tableLayout.childCount) {
            val row = tableLayout.getChildAt(i) as TableRow
            var isvisible = false

            for (j in 0 until row.childCount) {
                val textView = row.getChildAt(j) as TextView
                val text = textView.text.toString().lowercase(Locale.getDefault())
                if (text.contains(queryTex.orEmpty())) {
                    isvisible = true
                    break
                }
            }

            row.visibility = if (isvisible) View.VISIBLE else View.GONE

        }


    }


    private fun eliminarProducto() {

        if (binding.tvIdEditarCoca.text!!.isEmpty()) {
            Toast.makeText(requireContext(), "El campo id esta vacio ", Toast.LENGTH_LONG).show()

        } else if (binding.tvPrecioEditarCoca.text!!.isNotEmpty()) {
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
                    val id = binding.tvIdEditarCoca.text.toString().toInt()
                    cocaViewModel.eliminarProducto(id)
                    Toast.makeText(
                        requireContext(),
                        "Dato eliminado exitosamente!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.tvIdEditarCoca.setText("")
                    dialog.dismiss()

                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

    }


    private fun editarProducto() {
        val id = binding.tvIdEditarCoca.text.toString()
        val precio = binding.tvPrecioEditarCoca.text.toString()


        if (id.isNotEmpty() && precio.isNotEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("ADVERTENCIA")
                .setMessage("Los campos no pueden quedar vacios")
                .setPositiveButton("Continuar") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else {
            val idInt = id.toInt()
            val precioDouble = precio.toDouble()
            cocaViewModel.editarCocaTabla(idInt, precioDouble)
            Toast.makeText(requireContext(), "Dato editado exitosamente!!", Toast.LENGTH_SHORT)
                .show()
            binding.tvIdEditarCoca.setText("")
            binding.tvPrecioEditarCoca.setText("")

        }


    }


    private fun mostrarTabla(lists: List<TablaCoca>) {

        tableLayout.removeAllViews() // Limpiar la tabla antes de agregar nuevas filas

        for (row in lists) {
            val tableRow = LayoutInflater.from(requireContext())
                .inflate(R.layout.tabla_row_item, null) as TableRow

            val id = tableRow.findViewById<TextView>(R.id.tvIdR)
            id.text = row.id.toString()

            val producto = tableRow.findViewById<TextView>(R.id.tvProductoR)
            producto.text = row.producto

            val precio = tableRow.findViewById<TextView>(R.id.tvPrecioR)
            precio.text = row.precio.toString()

            // Agregar la fila a la tabla
            tableLayout.addView(tableRow)
        }
    }


}