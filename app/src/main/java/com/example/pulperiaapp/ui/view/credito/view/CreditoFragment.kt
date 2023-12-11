package com.example.pulperiaapp.ui.view.credito.view

import android.app.AlertDialog
import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentCreditoBinding
import com.example.pulperiaapp.ui.view.credito.adapter.AdapterAmoroso
import com.example.pulperiaapp.ui.view.credito.viewmodel.CreditoViewModel
import com.example.pulperiaapp.ui.view.principal.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreditoFragment : Fragment() {

    private val amorosoModel: CreditoViewModel by viewModels()

    private lateinit var binding: FragmentCreditoBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterAmoroso
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreditoBinding.inflate(layoutInflater, container, false)
        initComponent()

        amorosoModel.groupedAmorosoModel.observe(viewLifecycleOwner) { lista ->
            adapter.setLista(lista)
        }


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        amorosoModel.actualizarDatos()
        binding.btnRegistrarMoroso.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        binding.btnRegistrarMoroso.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_creditoFragment_to_amorosoFragment)
        }
        binding.shAmoroso.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.filter.filter(p0)
                return true

            }

        })


        val callBack = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            cerrarSesion()
        }
        callBack.isEnabled = true


    }

    private fun initComponent() {
        recyclerView = binding.rvAmoroso
        val managuer = LinearLayoutManager(requireContext())
        managuer.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = managuer
        adapter = AdapterAmoroso(
            onClickUpdate = { cliente -> actualizarPago(cliente) }, requireContext(),
            onClickSee = { cliente, id -> editar(cliente, id) }, false
        )
        recyclerView.adapter = adapter

    }

    private fun editar(cliente: String, id: Int) {
        findNavController().navigate(
            CreditoFragmentDirections.actionCreditoFragmentToEditarCreditoFragment(
                cliente, id
            )
        )

    }

    private fun actualizarPago(cliente: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("ADVERTENCIA")
            .setMessage("¿Estas seguro que desea cancelar el pago?")
            .setPositiveButton("Si") { dialog, _ ->
                amorosoModel.actualizarEstado(true, cliente)
                Toast.makeText(requireContext(), "Saldo de  $cliente cancelado ", Toast.LENGTH_LONG)
                    .show()
                dialog.dismiss()
            }
            .setNegativeButton("No") { diag, _ ->
                diag.dismiss()
            }

            .show()
    }


    fun cerrarSesion() {
        val alert = AlertDialog.Builder(requireContext())
            .setTitle("ADVERTENCIA")
            .setMessage("¿Estas seguro qeu desea cerrar sesion?")
            .setPositiveButton("Aceptar") { dialog, _ ->
                (activity as MainActivity).findViewById<BottomNavigationView>(R.id.NavigationBottom).isVisible =
                    false

                val navControllet = Navigation.findNavController(binding.root)
                navControllet.popBackStack()
            }

            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
        alert.show()
    }
}