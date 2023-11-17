package com.example.pulperiaapp.ui.view.credito

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
    ): View? {

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
        binding.tvVisualizarCliente.text = args.cliente
        binding.btnEditarCredito.setOnClickListener {
            editarCredito()
        }
    }

    private fun editarCredito() {
        lifecycleScope.launch {
            val cliente = args.cliente

            val fechaFormateada =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            productoRecuperado[cliente]?.forEach { detalle ->
                val prodcuto = detalle.producto
                val cantidad = detalle.cantidad
                val precio = detalle.precio_total
                val id = detalle.id

                creditoModel.editarCredito(id, prodcuto, cantidad, precio)
            }

            Toast.makeText(requireContext(), "Datos editados exitosamente!!!", Toast.LENGTH_LONG)
                .show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }


    private fun initDate() {
        val cliente = args.cliente
        lifecycleScope.launch {
            creditoModel.obtenerDetalleAmoroso(cliente)

            creditoModel.groupedAmorosoModel.observe(viewLifecycleOwner) { observer ->
                observer?.let {
                    for ((i, info) in observer) {
                        for (j in info) {
                            val id = j.id
                            val producto = j.producto
                            val cantidad = j.cantidad
                            val precio = j.precio_total
                            guardarDetalleRecuperado(id, producto, cantidad, precio)
                        }
                    }
                    actualizarTabla()
                }
            }
        }
    }


    private fun guardarDetalleRecuperado(id: Int, producto: String, cantidad: Int, precio: Double) {
        val cliente = args.cliente

        if (!productoRecuperado.containsKey(cliente)) {
            val lista = mutableListOf(DetalleAmoroso(id, producto, cantidad, precio))
            productoRecuperado[cliente] = lista
        } else {
            productoRecuperado[cliente]?.add(DetalleAmoroso(id, producto, cantidad, precio))
        }
    }


    private fun actualizarTabla() {

        tableLayout.removeAllViews()

        var totaRecuperado = 0.0
        val cliente = args.cliente
        productoRecuperado[cliente]?.forEach { map ->
            val idCliente = map.id
            val producto = map.producto
            val cantidad = map.cantidad
            val precio = map.precio_total
            agregarFila(producto, cantidad, precio, cliente, idCliente)
            totaRecuperado += precio
        }
        binding.tvTotalAmount.text = totaRecuperado.toString()


    }


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
            if (cantidad >= 1) {
                val nuevaCantidad = cantidad - 1
                val nuevoPrecio = nuevaCantidad * precio / cantidad

                val detalleExistente = productoRecuperado[pro]?.find { it.producto == producto }
                detalleExistente?.let {
                    it.cantidad = nuevaCantidad
                    it.precio_total = nuevoPrecio
                }

                actualizarTabla()
            } else {
                val detalleExistente = productoRecuperado[pro]?.find { it.producto == producto }
                detalleExistente?.let {
                    productoRecuperado[pro]?.remove(it)
                }
                binding.tvTotalAmount.text = 0.0.toString()
                actualizarTabla()
            }
        }

        cantidadView.setOnClickListener {
            if (cantidad >= 1) {
                val nuevaCantidad = cantidad + 1
                val nuevoPrecio = nuevaCantidad * precio / cantidad
                val detalleExistente = productoRecuperado[pro]?.find { it.producto == producto }
                detalleExistente?.let {
                    it.cantidad = nuevaCantidad
                    it.precio_total = nuevoPrecio
                }

                actualizarTabla()
            } else {
                val detalleExistente = productoRecuperado[pro]?.find { it.producto == producto }
                detalleExistente?.let {
                    productoRecuperado[pro]?.remove(it)
                }
                binding.tvTotalAmount.text = 0.0.toString()
                actualizarTabla()
            }
        }


        tableLayout.addView(tableRow)

    }


}