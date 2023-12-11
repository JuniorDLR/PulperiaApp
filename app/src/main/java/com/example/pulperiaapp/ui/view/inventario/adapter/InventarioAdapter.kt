package com.example.pulperiaapp.ui.view.inventario.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.InventarioItemBinding
import com.example.pulperiaapp.domain.inventario.InventarioModel


class InventarioAdapter
    (
    private val onClickDelete: (String) -> Unit,
    private val onClickUpdate: (String) -> Unit,


    ) :
    RecyclerView.Adapter<InventarioAdapter.ViewHolder>(), Filterable {

    var listaModel: Map<String, List<InventarioModel>> = emptyMap()
    var listaModelFilter: Map<String, List<InventarioModel>> = emptyMap()
    var nombre: String = ""


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = InventarioItemBinding.bind(view)

        fun bind(lista: InventarioModel) {
            binding.tvFechaInventario.text = lista.fecha_entrega
            binding.btnEditarInventario.setOnClickListener {
                onClickUpdate(lista.fecha_entrega)
            }
            binding.btnEliminarInventario.setOnClickListener {
                onClickDelete(lista.fecha_entrega)
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.inventario_item, parent, false)
        return ViewHolder(inflate)
    }

    override fun getItemCount(): Int = listaModelFilter.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val diag = listaModel.keys.elementAt(position)
        val lista = listaModel[diag]
        nombre = "Inventario: $position"
        holder.binding.NombreInventario.text = nombre
        if (lista != null) {
            holder.bind(lista[0])
        }


    }

    fun setList(lista: Map<String, List<InventarioModel>>) {
        listaModel = lista
        listaModelFilter = lista
        notifyDataSetChanged()

    }



    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {

                val queryTex = p0?.toString()?.trim() ?: ""
                listaModelFilter = if (queryTex.isEmpty()) {
                    listaModel
                } else {
                    listaModel.filter { (key, lista) ->
                        lista.any { it.fecha_entrega.contains(queryTex, ignoreCase = true) }
                    }
                }

                val filterResult = FilterResults()
                filterResult.values = listaModelFilter
                filterResult.count = listaModelFilter.size
                return filterResult
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }


}



