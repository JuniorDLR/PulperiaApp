package com.example.pulperiaapp.ui.view.venta.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
//_+uqd<nK-@NK$R8GT2i&
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.ItemVentaBinding
import com.example.pulperiaapp.domain.venta.VentaPrixCocaDetalle
import kotlin.collections.List


class AdapterVenta(
    private val onDeleteClickListener: (Int, Int, String) -> Unit,
    private val onUpdateClickListener: (String, Int) -> Unit
) : RecyclerView.Adapter<AdapterVenta.ViewHolder>(),
    Filterable {
    var listaVenta: Map<String, List<VentaPrixCocaDetalle>> = emptyMap()
    var filterList: Map<String, List<VentaPrixCocaDetalle>> = emptyMap()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemVentaBinding.bind(view)

        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(venta: VentaPrixCocaDetalle) {
            binding.tvFecha.text = venta.fecha_venta
            binding.tvListProducto.text =
                if (!venta.ventaPorCajilla) "${venta.producto} - ${venta.cantidad}" else "Venta por cajilla"
            binding.tvTotal.text = venta.total_venta.toString()

            binding.btnEliminarVenta.setOnClickListener {
                onDeleteClickListener(venta.id, bindingAdapterPosition, venta.fecha_venta)
            }
            binding.btnEditarVenta.setOnClickListener {
                onUpdateClickListener(venta.fecha_venta, venta.id)
            }
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
        val diagKey = filterList.values.elementAt(position)
        if (diagKey.isNotEmpty()) {
            holder.bind(diagKey[0])
        }
    }

    fun setListCajilla(newList: Map<String, List<VentaPrixCocaDetalle>>) {
        listaVenta = newList
        filterList = newList
        notifyDataSetChanged()
    }

    fun setListIndividual(newList: List<VentaPrixCocaDetalle>) {
        filterList = newList.associate { it.id.toString() to listOf(it) }
        notifyDataSetChanged()
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                val productoQuery = query?.toString()?.trim() ?: ""
                filterList = if (productoQuery.isEmpty()) {
                    listaVenta
                } else {
                    listaVenta.filter { (cl) ->
                        cl.contains(productoQuery, ignoreCase = true)

                    }


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