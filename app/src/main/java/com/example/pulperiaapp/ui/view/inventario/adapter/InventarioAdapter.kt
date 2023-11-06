package com.example.pulperiaapp.ui.view.inventario.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.InventarioItemBinding
import com.example.pulperiaapp.domain.inventario.InventarioModel


class InventarioAdapter
    (private val onClickDelete: (Int, Int) -> Unit) :
    RecyclerView.Adapter<InventarioAdapter.ViewHolder>() {

    var listaModel: Map<String, List<InventarioModel>> = emptyMap()
    var contador = 0

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = InventarioItemBinding.bind(view)

        fun bind(lista: InventarioModel) {
            binding.tvFechaInventario.text = lista.fecha_entrega


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.inventario_item, parent, false)
        return ViewHolder(inflate)
    }

    override fun getItemCount(): Int = listaModel.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val diag = listaModel.keys.elementAt(position)
        val lista = listaModel[diag]
        holder.binding.NombreInventario.text = inventarioDinamico()
        val id = lista?.first()?.id
        holder.binding.btnEliminar.setOnClickListener {
            if (id != null) {
                onClickDelete(position, id)
            }
        }
        if (lista != null) {
            holder.bind(lista[0])
        }


    }

    fun setList(lista: Map<String, List<InventarioModel>>) {
        listaModel = lista
        notifyDataSetChanged()

    }

    fun inventarioDinamico(): String {
        val nombreInventario = "Inventario $contador"
        contador++

        return nombreInventario

    }

}