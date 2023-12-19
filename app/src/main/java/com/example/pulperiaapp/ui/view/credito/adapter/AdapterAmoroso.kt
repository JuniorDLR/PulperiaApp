package com.example.pulperiaapp.ui.view.credito.adapter


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.ItemAmorosoBinding
import com.example.pulperiaapp.domain.amoroso.VentaAmorosoDetalle


class AdapterAmoroso(
    private val onClickUpdate: (String) -> Unit = { _ -> },
    private val context: Context,
    private val onClickSee: (String, Int) -> Unit = { _, _ -> },
    private val isEspecter: Boolean

) : RecyclerView.Adapter<AdapterAmoroso.MyHolder>(), Filterable {
    private var listaAmoroso: Map<String, List<VentaAmorosoDetalle>> = emptyMap()
    private var filterable: Map<String, List<VentaAmorosoDetalle>> = emptyMap()
    private val datosItem = mutableMapOf<String, MutableList<VentaAmorosoDetalle>>()


    inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemAmorosoBinding.bind(view)
        fun bind(diag: VentaAmorosoDetalle) {
            val cliente = diag.cliente
            val id = diag.id
            binding.tvCliente.text = cliente
            binding.itemAmoroso.setOnClickListener {
                mostrarDialogoClienteDetalle(cliente, id)

            }
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


            val lista = mutableListOf<VentaAmorosoDetalle>()

            for (i in detallesCliente) {
                val id = i.id
                val producto = i.producto
                val cantidad = i.cantidad
                val precio = i.precioTotal

                val fecha = i.fecha

                val detalle = VentaAmorosoDetalle(
                    id, cliente, producto, cantidad, precio, fecha, false

                )

                lista.add(detalle)

                datosItem[cliente] = lista
            }

            holder.bind(detallesCliente[0])
        }
    }

    @SuppressLint("InflateParams")
    private fun mostrarDialogoClienteDetalle(cliente: String, id: Int) {
        val dialog = AlertDialog.Builder(context)
        val scrollView = ScrollView(context)
        val contentView = LayoutInflater.from(context).inflate(R.layout.detalle_cliente, null)
        val tlProducto = contentView.findViewById<TableLayout>(R.id.tlProducto)
        val resources = context.resources

        var totaPagar = 0.0
        datosItem[cliente]?.forEach { info ->
            val precioTotal = info.precioTotal
            totaPagar += precioTotal

            val row = TableRow(context)
            val layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )

            // Agregar márgenes inferiores a cada fila
            layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.row_bottom_margin)
            row.layoutParams = layoutParams

            val clienteView = contentView.findViewById<TextView>(R.id.tvClienteDetalle)
            clienteView.text = cliente

            val productoView = TextView(context)
            productoView.text = info.producto

            val cantidadView = TextView(context)
            cantidadView.text = info.cantidad.toString()

            val fechaView = TextView(context)
            fechaView.text = info.fecha

            val precioView = TextView(context)
            precioView.text = info.precioTotal.toString()

            row.addView(productoView)
            row.addView(cantidadView)
            row.addView(fechaView)
            row.addView(precioView)

            tlProducto.addView(row)

            // Agregar una vista de espacio entre cada fila
            val spaceView = View(context)
            spaceView.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.row_space_height)
            )
            tlProducto.addView(spaceView)
        }


        val total = contentView.findViewById<TextView>(R.id.tvTotalAmoroso)
        total.text = totaPagar.toString()

        if (!isEspecter) {
            dialog.setPositiveButton("Realizar pago") { diag, _ ->
                onClickUpdate(cliente)
                diag.dismiss()
            }
            dialog.setNegativeButton("Editar") { diag, _ ->
                onClickSee(cliente, id)
                diag.dismiss()
            }
        }

        scrollView.addView(contentView)
        dialog.setView(scrollView)
        dialog.show()

    }

    @SuppressLint("NotifyDataSetChanged")
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

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }


}