package com.example.pulperiaapp.domain.venta

import androidx.room.Embedded
import com.example.pulperiaapp.data.database.entitie.venta.VentaPrixCoca


data class VentaPrixCocaDetalle(
    @Embedded val venta: VentaPrixCoca
) {
    val producto: List<String>
        get() = venta.producto.split(",").filter { it.isNotBlank() }
    val total_venta: Double
        get() = venta.total
    val fecha_venta: Long
        get() = venta.fecha
    val cantidad: List<String>
        get() = venta.cantidad.split(",").filter { it.isNotBlank() }
}








