package com.example.pulperiaapp.ui.view.venta.adapter



import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.apachat.swipereveallayout.core.ViewBinder
import androidx.recyclerview.widget.RecyclerView
import com.apachat.swipereveallayout.core.SwipeLayout
//_+uqd<nK-@NK$R8GT2i&
import com.example.pulperiaapp.databinding.ItemVentaBinding
import com.example.pulperiaapp.domain.venta.VentaPrixCocaDetalle
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.collections.List


class AdapterVenta(
    private val onDeleteClickListener: (Int, Int, String) -> Unit = { _, _, _ -> },
    private val onUpdateClickListener: (String, Int) -> Unit = { _, _ -> }

) : RecyclerView.Adapter<AdapterVenta.ViewHolder>(),
    Filterable {
    var listaVenta: Map<String, List<VentaPrixCocaDetalle>> = emptyMap()
    var filterList: Map<String, List<VentaPrixCocaDetalle>> = emptyMap()

    private var currentTabPosition: Int = 0


    private val viewBinderHelper: ViewBinder = ViewBinder()


    inner class ViewHolder(private val binding: ItemVentaBinding) :
        RecyclerView.ViewHolder(binding.root) {


        val swipeLayout: SwipeLayout = binding.swipeLayout
        fun bind(venta: VentaPrixCocaDetalle) {
            binding.tvFecha.text = venta.fechaVenta
            binding.tvListProducto.text = if (!venta.ventaPorCajilla) "${venta.producto} - ${venta.cantidad}" else "Venta por cajilla"

            val totalVentasCajillaPorFecha = listaVenta[venta.fechaVenta]
                ?.filter { it.ventaPorCajilla }
                ?.sumOf { it.totalVenta }

            val precioFormateadoCajilla = totalVentasCajillaPorFecha?.let { formatearPrecio(it) }
            val precioFormateadoIndividual = formatearPrecio(venta.totalVenta)

            binding.tvTotal.text =
                if (!venta.ventaPorCajilla) precioFormateadoIndividual else precioFormateadoCajilla


            binding.btnDel.setOnClickListener {
                onDeleteClickListener(venta.id, bindingAdapterPosition, venta.fechaVenta)
            }
            binding.btnEdit.setOnClickListener {
                onUpdateClickListener(venta.fechaVenta, venta.id)
            }

        }

    }

    private fun formatearPrecio(precio: Double): String? {
        val bigDecimal = BigDecimal.valueOf(precio)
        val format = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.getDefault()))
        return format.format(bigDecimal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemVentaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int = filterList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val diagKey = filterList.values.elementAt(position)
        if (diagKey.isNotEmpty()) {
            val venta = diagKey[0]
            holder.bind(venta)
            viewBinderHelper.setOpenOnlyOne(true)
            viewBinderHelper.bind(holder.swipeLayout, venta.id.toString())
            viewBinderHelper.closeLayout(venta.id.toString())
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setListCajilla(newList: Map<String, List<VentaPrixCocaDetalle>>) {
        listaVenta = newList
        filterList = newList
        notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
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
                    if (currentTabPosition == 0) {

                        filterList.filter { (_, ventas) ->
                            ventas.any { it.fechaVenta.contains(productoQuery, ignoreCase = true) }

                        }
                    } else {

                        listaVenta.filter { (_, ventas) ->
                            ventas.any { it.fechaVenta.contains(productoQuery, ignoreCase = true) }
                        }
                    }
                }


                val result = FilterResults()
                result.values = filterList
                result.count = filterList.size
                return result
            }


            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }


    fun verificacion(contexto: Int) {
        currentTabPosition = contexto

    }


    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(position: Int) {
        filterList = filterList.toMutableMap().apply {
            remove(keys.elementAt(position))
        }
        notifyDataSetChanged()
    }


}
