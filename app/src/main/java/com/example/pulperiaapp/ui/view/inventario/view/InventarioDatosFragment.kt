package com.example.pulperiaapp.ui.view.inventario.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Build

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation


import java.util.Calendar
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentInventarioDatosBinding
import com.example.pulperiaapp.ui.view.inventario.adapter.InventarioAdapter
import com.example.pulperiaapp.ui.view.inventario.viewmodel.InventarioViewModel
import com.example.pulperiaapp.ui.view.principal.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class InventarioDatosFragment : Fragment() {

    private lateinit var binding: FragmentInventarioDatosBinding
    private val inventarioModel: InventarioViewModel by viewModels()
    private lateinit var adapter: InventarioAdapter
    private lateinit var recyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentInventarioDatosBinding.inflate(inflater, container, false)





        binding.btnAgregarInventario.setColorFilter(
            ContextCompat.getColor(
                requireContext(), R.color.white
            )
        )
        return binding.root
    }


    private fun initComponent() {
        recyclerView = binding.rvInventario
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager

        adapter = InventarioAdapter(
            onClickDelete = { fecha -> eliminarItem(fecha) },
            onClickUpdate = { idFecha -> updateItem(idFecha) }
        )
        recyclerView.adapter = adapter

    }


    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(
                requireContext(),
                R.style.MyDatePickerDialogTheme,
                { _, selectYear, selectMonth, selecteDay ->
                    val selectDay = Calendar.getInstance()
                    selectDay.set(selectYear, selectMonth, selecteDay)

                    val dataFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formatDate = dataFormat.format(selectDay.time)
                    binding.etFecha.setText(formatDate)

                }, year, month, day
            )
        datePickerDialog.show()

    }

    private fun updateItem(idFecha: String) {
        findNavController().navigate(
            InventarioDatosFragmentDirections.actionInventarioDatosFragmentToEditarInventarioFragment(
                idFecha
            )
        )

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun eliminarItem(fecha: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("ADVERTENCIA")
            .setMessage("¿Estás seguro que deseas eliminarlo?")
            .setPositiveButton("Sí") { dialog, _ ->
                lifecycleScope.launch {
                    try {
                        // Obtén la lista completa de inventarios
                        val inventariosCompletos = inventarioModel.obtenerDetalleInventario(fecha)

                        // Filtra la lista para obtener solo los inventarios con la misma fecha
                        val inventariosAEliminar =
                            inventariosCompletos.filter { it.fechaEntrega == fecha }

                        // Lógica de eliminación, podrías usar un bucle para eliminar uno por uno
                        for (inventario in inventariosAEliminar) {
                            inventarioModel.eliminarInventario(inventario.id)
                        }

                        // Notificar al adaptador que los datos han cambiado
                        adapter.notifyDataSetChanged()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponent()

        inventarioModel.obtenerInventario()
        inventarioModel.inventarioModel.observe(viewLifecycleOwner) { lista ->
            adapter.setList(lista)
        }

        binding.etFecha.setOnClickListener { showDatePicker() }
        binding.btnAgregarInventario.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.inventarioFragment)
        }

        val callBck = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            cerrarSesion()
        }
        callBck.isEnabled = true

        binding.etFecha.inputType = InputType.TYPE_NULL
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter.filter(p0)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        }
        binding.etFecha.addTextChangedListener(textWatcher)

    }

    private fun cerrarSesion() {
        val alert = AlertDialog.Builder(requireContext())
            .setTitle("ADVERTENCIA")
            .setMessage("¿Estas seguro que desea cerrar sesion?")
            .setPositiveButton("Aceptar") { dialog, _ ->
                (activity as MainActivity).findViewById<BottomNavigationView>(R.id.NavigationBottom).isVisible =
                    false
                val navController = Navigation.findNavController(binding.root)
                navController.popBackStack()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

        alert.show()
    }
}