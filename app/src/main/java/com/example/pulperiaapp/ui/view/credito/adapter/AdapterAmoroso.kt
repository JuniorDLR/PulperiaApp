package com.example.pulperiaapp.ui.view.credito.adapter

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.ItemAmorosoBinding
import com.example.pulperiaapp.domain.amoroso.VentaAmorosoDetalle


class AdapterAmoroso(
    private val onClickUpdate: (String) -> Unit,
    private val context: Context

) :
    RecyclerView.Adapter<AdapterAmoroso.MyHolder>(), Filterable {
    private var listaAmoroso: Map<String, List<VentaAmorosoDetalle>> = emptyMap()
    private var filterable: Map<String, List<VentaAmorosoDetalle>> = emptyMap()


    inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemAmorosoBinding.bind(view)

        fun bind(diag: VentaAmorosoDetalle) {

            val cliente = diag.cliente
            binding.tvCliente.text = cliente

        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.item_amoroso, parent, false)
        return MyHolder(inflate)
    }

    override fun getItemCount(): Int = filterable.size

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val cliente = filterable.keys.elementAt(position)
        val detallesCliente = filterable[cliente]


        if (detallesCliente != null) {

            val detallesCompra = StringBuilder()
            var precioTotalCliente = 0.0

            for (detalle in detallesCliente) {
                val fecha = detalle.fecha.replace("[", "").replace("]", "")
                val producto = detalle.producto.joinToString("")
                val cantidad = detalle.cantidad.joinToString("")
                val precioTotalDetalle = detalle.precio_total

                detallesCompra.append("$producto - $cantidad - $fecha\n")
                precioTotalCliente += precioTotalDetalle
            }




            holder.itemView.setOnClickListener {
                mostrarDialogoClienteDetalle(
                    cliente,
                    detallesCompra.toString(),
                    precioTotalCliente
                )
            }
            holder.bind(detallesCliente[0])
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun mostrarDialogoClienteDetalle(
        cliente: String,
        detalles: String,
        precioTotalCliente: Double
    ) {
        val dialog = AlertDialog.Builder(context)
        val scrollView = ScrollView(context)
        val contentView = LayoutInflater.from(context).inflate(R.layout.detalle_cliente, null)


        val clienteView = contentView.findViewById<TextView>(R.id.tvNombreCliente)
        clienteView.text = cliente

        val productoView = contentView.findViewById<TextView>(R.id.tvListProductoDetalle)
        productoView.text = detalles

        val precioView = contentView.findViewById<TextView>(R.id.tvPrecioTotal)
        precioView.text = precioTotalCliente.toString()

        dialog.setPositiveButton("Realizar pago") { diag, _ ->
            onClickUpdate(cliente)
        }


        scrollView.addView(contentView)
        dialog.setView(scrollView)

        dialog.show()
    }


    fun setLista(lista: Map<String, List<VentaAmorosoDetalle>>) {
        listaAmoroso = lista
        filterable = lista
        notifyDataSetChanged()
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(texto: CharSequence?): FilterResults {

                val query = texto?.toString()?.trim() ?: ""
                filterable = if (query.isEmpty()) {
                    listaAmoroso
                } else {
                    listaAmoroso.filter { (cl) ->
                        cl.contains(query, ignoreCase = true)
                    }
                }

                val result = FilterResults()
                result.count = filterable.size
                result.values = filterable
                return result

            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }


}