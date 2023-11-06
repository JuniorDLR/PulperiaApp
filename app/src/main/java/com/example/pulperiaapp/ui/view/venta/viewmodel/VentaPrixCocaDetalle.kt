package com.example.pulperiaapp.ui.view.venta.viewmodel

import com.example.pulperiaapp.data.database.entitie.VentaPrixCoca


data class VentaPrixCocaDetalle(
    val id: Int,
    val producto: String,
    val total_venta: Double,
    val fecha_venta: Long,
    val ventaPorCajilla: Boolean,
    val cantidad: Int,
)

fun VentaPrixCoca.toDomain() = VentaPrixCocaDetalle(
    id = id,
    producto = producto,
    total_venta = total,
    fecha_venta = fecha,
    ventaPorCajilla = ventaPorCajilla,
    cantidad = cantidad
)









