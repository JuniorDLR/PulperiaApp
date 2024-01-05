package com.example.pulperiaapp.ui.view.credito.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.FragmentEditarCreditoBinding
import com.example.pulperiaapp.domain.amoroso.DetalleAmoroso
import com.example.pulperiaapp.ui.view.credito.viewmodel.CreditoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class EditarCreditoFragment : Fragment() {

    private val args: EditarCreditoFragmentArgs by navArgs()
    private lateinit var binding: FragmentEditarCreditoBinding
    private val creditoModel: CreditoViewModel by viewModels()
    private lateinit var tableLayout: TableLayout
    private lateinit var tableRow: TableRow
    private val productoRecuperado = mutableMapOf<String, MutableList<DetalleAmoroso>>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentEditarCreditoBinding.inflate(inflater, container, false)

        initComponent()
        return binding.root
    }


    private fun initComponent() {
        tableLayout = binding.tlProductoEditarCredito
        tableRow = binding.trProducto

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDate()
        val cliente = "Cliente - ${args.cliente}"
        binding.tvVisualizarCliente.text = cliente
        binding.btnEditarCredito.setOnClickListener {
            editarCredito()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_editarCreditoFragment_to_creditoFragment)
        }
    }

    private fun editarCredito() {
        lifecycleScope.launch {
            val cliente = args.cliente

            val fechaFormateada =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            productoRecuperado[cliente]?.forEach { detalle ->
                val producto = detalle.producto
                val cantidad = detalle.cantidad
                val precio = detalle.precioTotal
                val id = detalle.id

                if (cantidad > 0) {
                    creditoModel.editarCredito(id, producto, cantidad, precio, fechaFormateada)
                } else {
                    Log.e("Eliminar", "entrnado a eliminacion  $id")
                    creditoModel.eliminarCredito(id)
                }
            }

            Toast.makeText(requireContext(), "Datos editados exitosamente!!!", Toast.LENGTH_LONG)
                .show()
            val action =
                EditarCreditoFragmentDirections.actionEditarCreditoFragmentToCreditoFragment()
            Navigation.findNavController(binding.root).navigate(action)
        }
    }


    private fun initDate() {
        val cliente = args.cliente
        lifecycleScope.launch {
            creditoModel.obtenerDetalleAmoroso(cliente)

            creditoModel.groupedAmorosoModel.observe(viewLifecycleOwner) { observer ->
                observer?.let {
                    for ((_, info) in observer) {
                        for (j in info) {
                            val id = j.id
                            val producto = j.producto
                            val cantidad = j.cantidad
                            val precio = j.precioTotal

                            guardarDetalleRecuperado(id, producto, cantidad, precio)
                        }
                    }
                    actualizarTabla()
                }
            }
        }
    }


    private fun guardarDetalleRecuperado(
        id: Int,
        producto: String,
        cantidad: Int,
        precio: Double,

        ) {
        val cliente = args.cliente

        if (cantidad > 0) {
            if (!productoRecuperado.containsKey(cliente)) {
                val lista = mutableListOf(DetalleAmoroso(id, producto, cantidad, precio))
                productoRecuperado[cliente] = lista
            } else {
                productoRecuperado[cliente]?.add(
                    DetalleAmoroso(
                        id,
                        producto,
                        cantidad,
                        precio,

                        )
                )
            }
        }
    }


    private fun actualizarTabla() {
        tableLayout.removeAllViews()

        var totaRecuperado = 0.0
        val cliente = args.cliente
        productoRecuperado[cliente]?.filter { it.cantidad > 0 }?.forEach { map ->
            val producto = map.producto
            val cantidad = map.cantidad
            val precio = map.precioTotal
            agregarFila(producto, cantidad, precio, cliente, map.id)
            totaRecuperado += precio
        }
        binding.tvTotalAmount.text = totaRecuperado.toString()
    }


    @SuppressLint("InflateParams")
    private fun agregarFila(
        producto: String,
        cantidad: Int,
        precio: Double,
        pro: String,
        idCliente: Int
    ) {


        val tableRow = LayoutInflater.from(requireContext())
            .inflate(R.layout.table_row_venta, null) as TableRow
        tableRow.tag = idCliente

        val productoView = tableRow.findViewById<TextView>(R.id.tvProductoVenta)
        productoView.text = producto

        val cantidadView = tableRow.findViewById<TextView>(R.id.tvCantidadVenta)
        cantidadView.text = cantidad.toString()

        val precioView = tableRow.findViewById<TextView>(R.id.tvPrecioVenta)
        precioView.text = precio.toString()


        productoView.setOnClickListener {
            if (cantidad >1) {
                val nuevaCantidad = cantidad - 1
                val nuevoPrecio = nuevaCantidad * precio / cantidad

                val detalleExistente = productoRecuperado[pro]?.find { it.id == idCliente }
                detalleExistente?.let {
                    it.cantidad = nuevaCantidad
                    it.precioTotal = nuevoPrecio
                }

                actualizarTabla()
            } else {
                val detalleExistente = productoRecuperado[pro]?.find { it.id == idCliente }
                detalleExistente?.let {
                    productoRecuperado[pro]?.remove(it)

                }
                binding.tvTotalAmount.text = 0.0.toString()
                actualizarTabla()
            }
        }

        cantidadView.setOnClickListener {


            showQuantityDialog(cantidadView, precio, cantidad, idCliente, pro)
        }


        tableLayout.addView(tableRow)

    }

    private fun showQuantityDialog(
        cantidadView: TextView,
        precio: Double,
        cantidad: Int,
        idCliente: Int,
        pro: String
    ) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Modificar cantidad")
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.setText(cantidadView.text)

        builder.setView(input)
        builder.setPositiveButton("Aceptar") { dialog, _ ->

            val nuevaCantidad = input.text.toString().toInt()
            val nuevoPrecio = nuevaCantidad * precio / cantidad


            productoRecuperado[pro]?.find { it.id == idCliente }?.apply {
                this.cantidad = nuevaCantidad
                this.precioTotal = nuevoPrecio

            }

            actualizarTabla()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()

    }


}