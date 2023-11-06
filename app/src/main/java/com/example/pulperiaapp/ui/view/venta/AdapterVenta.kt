package com.example.pulperiaapp.ui.view.venta

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.ItemVentaBinding
import com.example.pulperiaapp.domain.venta.VentaPrixCocaDetalle
import java.text.SimpleDateFormat
import kotlin.collections.List
import java.util.Date
import java.util.Locale

class AdapterVenta() : RecyclerView.Adapter<AdapterVenta.ViewHolder>(), Filterable {
    private var listaVenta: List<VentaPrixCocaDetalle> = emptyList()
    private var filterList: List<VentaPrixCocaDetalle> = emptyList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemVentaBinding.bind(view)

        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(venta: VentaPrixCocaDetalle) {
            val fecha = venta.fecha_venta
            val fechaFormateada =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(fecha))

            // Separa los productos y cantidades
            val productos = venta.producto
            val cantidades = venta.cantidad
            val total = venta.total_venta


            binding.tvListProducto.text = "$productos - $cantidades"
            binding.tvTotal.text = total.toString()
            binding.tvFecha.text = fechaFormateada
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(parent.context).inflate(
            R.layout.item_venta, parent, false
        )
        return ViewHolder(inflate)
    }

    override fun getItemCount(): Int = filterList.size


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val diag = filterList[position]
        holder.bind(diag)
    }


    fun setList(newList: List<VentaPrixCocaDetalle>) {
        listaVenta = newList
        filterList = newList
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                val productoQuery = query?.toString()?.trim() ?: ""
                filterList = if (productoQuery.isEmpty()) {
                    listaVenta
                } else {
                    listaVenta.filter { producto ->
                        producto.producto.contains(productoQuery)
                    }.toMutableList()


                }
                val result = FilterResults()
                result.values = filterList
                result.count = filterList.size
                return result
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }


}