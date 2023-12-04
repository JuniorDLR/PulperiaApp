package com.example.pulperiaapp.ui.view.bigcola.view

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentTablaBigColaBinding
import com.example.pulperiaapp.domain.bigcola.TablaBig
import com.example.pulperiaapp.ui.view.bigcola.viewmodel.BigColaViewModel
import dagger.hilt.android.AndroidEntryPoint
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

        bigColaViewModel.obtenerBigcola()
        tableLayout = binding.tlProductoBig
        bigColaViewModel.bigModdel.observe(viewLifecycleOwner) { lista ->
            actualizarTabla(lista)
        }
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

        binding.btnAgregarProductoBig.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.agregandoBigFragment)
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


    private fun actualizarTabla(lista: List<TablaBig>) {
        tableLayout.removeAllViews()


        for (big in lista) {

            val tableRow =
                LayoutInflater.from(requireContext())
                    .inflate(R.layout.tabla_row_item, null) as TableRow

            val idView = tableRow.findViewById<TextView>(R.id.tvIdR)
            idView.text = big.id.toString()

            val productoView = tableRow.findViewById<TextView>(R.id.tvProductoR)
            productoView.text = big.producto

            val precioView = tableRow.findViewById<TextView>(R.id.tvPrecioR)
            precioView.text = big.precio.toString()
            tableLayout.addView(tableRow)
        }


    }


    private fun editarBigCola() {
        val id = binding.tvIdEditarBig.text.toString()
        val precio = binding.tvPrecioEditarBig.text.toString()

        if (id.isNotEmpty() && precio.isNotEmpty()) {
            val precioDouble = precio.toDouble()
            val idInt = id.toInt()
            bigColaViewModel.editarBigCola(precioDouble, idInt)
            binding.tvIdEditarBig.setText("")
            binding.tvPrecioEditarBig.setText("")
            Toast.makeText(requireContext(), "Producto editado exitosamente", Toast.LENGTH_LONG)
                .show()


        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("ADVERTENCIA")
                .setMessage("Los campos no pueden quedar vacios")
                .setPositiveButton("Continuar") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }

    private fun eliminarBigCola() {
        val id = binding.tvIdEditarBig.text.toString()
        val precio = binding.tvPrecioEditarBig.text.toString()

        if (id.isNotEmpty() && precio.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("ALERTA PRODUCTO")
                .setMessage("¿Estas seguro que deseas eliminarlo?")
                .setPositiveButton("Aceptar") { dialog, _ ->
                    val idInt = id.toInt()
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
        } else {
            Toast.makeText(
                requireContext(),
                "Para eliminar solo se debe introducir el id ",
                Toast.LENGTH_LONG
            ).show()
        }


    }

}