package com.example.pulperiaapp.domain.amoroso


import com.example.pulperiaapp.data.database.entitie.CreditoEntity

data class VentaAmorosoDetalle(
    val id: Int,
    val cliente: String,
    val producto: String,
    val cantidad: Int,
    val precio_total: Double,
    val fecha: String,
    var estado_pago: Boolean
)


fun CreditoEntity.toDomain(): VentaAmorosoDetalle {
    return VentaAmorosoDetalle(
        id = id,
        cliente = this.cliente,
        producto = this.producto,
        cantidad = this.cantidad,
        precio_total = this.precioTotal,
        fecha = this.fecha,
        estado_pago = this.estado_pago
    )
}



