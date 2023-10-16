package com.example.pulperiaapp.data.database.entitie.venta

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pulperiaapp.ui.view.venta.viewmodel.VentaPrixCocaDetalle


@Entity("tbl_venta_prix_coca")
data class VentaPrixCoca(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Int = 0,
    @ColumnInfo("producto") val producto: String,
    @ColumnInfo("total_venta") val total: Double,
    @ColumnInfo("fecha_venta") val fecha: Long,
    @ColumnInfo("cantidad") val cantidad: String
)

fun VentaPrixCocaDetalle.toDomain(): VentaPrixCoca {
    return VentaPrixCoca(
        0,
        producto = producto.joinToString(","),
        total = total_venta,
        fecha = fecha_venta,
        cantidad = cantidad.joinToString(",")
    )
}


