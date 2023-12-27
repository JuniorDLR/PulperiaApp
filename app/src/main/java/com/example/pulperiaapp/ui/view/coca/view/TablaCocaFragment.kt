package com.example.pulperiaapp.ui.view.coca.view


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
import com.example.pulperiaapp.databinding.FragmentTablaCocaBinding
import com.example.pulperiaapp.domain.coca.TablaCoca
import com.example.pulperiaapp.ui.view.coca.viewmodel.CocaViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale


@AndroidEntryPoint
class TablaCocaFragment : Fragment() {

    private lateinit var binding: FragmentTablaCocaBinding
    private val cocaViewModel: CocaViewModel by viewModels()
    private lateinit var searchView: SearchView
    private lateinit var tableLayout: TableLayout



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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_TablaCocaFragment_to_homeFragment)
        }

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
        val idEliminar = binding.tvIdEditarCoca.text.toString()
        val precioEliminar = binding.tvPrecioEditarCoca.text.toString()


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
                    val id = binding.tvIdEditarCoca.text.toString().toInt()
                    cocaViewModel.eliminarProducto(id)
                    Toast.makeText(
                        requireContext(),
                        "Datos eliminado exitosamente!!",
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


        if (id.isEmpty() || precio.isEmpty()) {
            Snackbar.make(requireView(), "Los campos no pueden quedar vacio", Snackbar.LENGTH_SHORT)
                .show()
        } else if (id.isEmpty() && precio.isNotEmpty() || id.isNotEmpty() && precio.isEmpty()) {
            Snackbar.make(requireView(), "Los campos no pueden quedar vacio", Snackbar.LENGTH_SHORT)
                .show()
        } else {
            val idInt = id.toInt()
            val precioDouble = precio.toDouble()

            lifecycleScope.launch {
                val obtenerPrecio = cocaViewModel.obtenerPrecioId(idInt)
                if (precioDouble.equals(obtenerPrecio)) {
                    Snackbar.make(requireView(), "El precio es el mismo", Snackbar.LENGTH_SHORT)
                        .show()

                } else {
                    cocaViewModel.editarCocaTabla(idInt, precioDouble)
                    limpiarCampos()
                    Toast.makeText(
                        requireContext(),
                        "Datos editado exitosamente!!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }


    }

    private fun limpiarCampos() {
        binding.tvIdEditarCoca.setText("")
        binding.tvPrecioEditarCoca.setText("")
    }


    @SuppressLint("InflateParams")
    private fun mostrarTabla(lists: List<TablaCoca>) {

        tableLayout.removeAllViews()

        for (row in lists) {
            val idProdcuto = row.id
            val producto = row.producto
            val precio = row.precio
            actualizarTabla(idProdcuto, producto, precio)
        }
    }

    @SuppressLint("InflateParams")
    private fun actualizarTabla(idProdcuto: Int, producto: String, precio: Double) {
        val tableRow = LayoutInflater.from(requireContext())
            .inflate(R.layout.tabla_row_item, null) as TableRow

        val idView = tableRow.findViewById<TextView>(R.id.tvIdR)
        idView.text = idProdcuto.toString()

        val productoView = tableRow.findViewById<TextView>(R.id.tvProductoR)
        productoView.text = producto

        val precioView = tableRow.findViewById<TextView>(R.id.tvPrecioR)
        precioView.text = precio.toString()


        idView.setOnClickListener {
            binding.tvIdEditarCoca.setText(idProdcuto.toString())
            binding.tvPrecioEditarCoca.setText("")
        }

        productoView.setOnClickListener {
            binding.tvIdEditarCoca.setText(idProdcuto.toString())
            binding.tvPrecioEditarCoca.setText(precio.toString())
        }

        // Agregar la fila a la tabla
        tableLayout.addView(tableRow)
    }


}