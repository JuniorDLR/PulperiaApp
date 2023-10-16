package com.example.pulperiaapp.ui.view.credito.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.RecyclerView
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.ItemAmorosoBinding
import com.example.pulperiaapp.domain.amoroso.VentaAmorosoDetalle
import com.example.pulperiaapp.ui.view.credito.viewmodel.CreditoViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


class AdapterAmoroso() :
    RecyclerView.Adapter<AdapterAmoroso.MyHolder>(), Filterable {
    private var listaAmoroso: List<VentaAmorosoDetalle> = emptyList()
    private var filterable: List<VentaAmorosoDetalle> = emptyList()


    inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemAmorosoBinding.bind(view)

        fun bind(diag: VentaAmorosoDetalle) {
            val fecha = diag.fecha
            val fechaFormateada =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(fecha)

            val producto = diag.producto
            val cantidad = diag.cantidad
            var estado = diag.estado_pago

            val cantidadConProdcuto =
                producto.zip(cantidad) { producto, cantidad -> "$producto - $cantidad" }
                    .joinToString(" , ")
            binding.tvCliente.text = diag.cliente
            binding.tvListProducto.text = cantidadConProdcuto
            binding.tvTotal.text = diag.precio_total.toString()
            binding.tvFecha.text = fechaFormateada
            binding.btnEstado.setOnClickListener {
                val alert = AlertDialog.Builder(itemView.context)
                    .setTitle("ADVERTENCIA")
                    .setMessage("¿El cliente ya pago?")
                    .setPositiveButton("Si") { dialog, _ ->
                        binding.tvEstado.setImageResource(R.drawable.verificacion)

                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }

                alert.show()
            }


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.item_amoroso, parent, false)
        return MyHolder(inflate)
    }

    override fun getItemCount(): Int = filterable.size
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val diag = filterable[position]
        holder.bind(diag)
    }

    fun setLista(lista: List<VentaAmorosoDetalle>) {
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
                    listaAmoroso.filter { producto ->
                        producto.producto.contains(query)
                    }.toMutableList()
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