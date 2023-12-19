package com.example.pulperiaapp.domain.venta

import com.example.pulperiaapp.data.database.entitie.VentaPrixCoca


data class VentaPrixCocaDetalle(
    val id: Int,
    val producto: String,
    val totalVenta: Double,
    val fechaVenta: String,
    val ventaPorCajilla: Boolean,
    val cantidad: Int
)

fun VentaPrixCoca.toDomain() = VentaPrixCocaDetalle(
    id = id,
    producto = producto,
    totalVenta = total,
    fechaVenta = fecha,
    ventaPorCajilla = ventaPorCajilla,
    cantidad = cantidad

)









