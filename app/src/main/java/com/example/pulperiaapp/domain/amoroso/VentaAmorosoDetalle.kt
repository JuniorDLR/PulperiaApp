package com.example.pulperiaapp.domain.amoroso


import com.example.pulperiaapp.data.database.entitie.CreditoEntity

data class VentaAmorosoDetalle(
    val id: Int,
    val cliente: String,
    val producto: String,
    val cantidad: Int,
    val precioTotal: Double,
    val fecha: String,
    var estadoPago: Boolean
)


fun CreditoEntity.toDomain(): VentaAmorosoDetalle {
    return VentaAmorosoDetalle(
        id = id,
        cliente = this.cliente,
        producto = this.producto,
        cantidad = this.cantidad,
        precioTotal = this.precioTotal,
        fecha = this.fecha,
        estadoPago = this.estadoPago
    )
}



