package com.example.pulperiaapp.ui.view.filtrado

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.ItemFilterBinding


class AdapterCliente
    : RecyclerView.Adapter<AdapterCliente.ViewHolder>() {

    private var listaCliente: List<String> = emptyList()


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemFilterBinding.bind(view)
        fun bind(nombre: String) {
            binding.textViewCliente.text = nombre
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(parent.context).inflate(
            R.layout.item_filter, parent, false
        )

        return ViewHolder(inflate)
    }

    override fun getItemCount(): Int = listaCliente.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val diag = listaCliente[position]
        holder.bind(diag)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(lista: List<String>) {
        listaCliente = lista
        notifyDataSetChanged()
    }


}