package com.example.pulperiaapp.domain.amoroso


import com.example.pulperiaapp.data.database.entitie.credito.CreditoEntity

data class VentaAmorosoDetalle(
    val cliente: String,
    val producto: List<String>,
    val cantidad: List<String>,
    val precio_total: Double,
    val fecha: Long,
    var estado_pago: Boolean
)


fun CreditoEntity.toDomain(): VentaAmorosoDetalle {
    return VentaAmorosoDetalle(
        cliente = this.cliente,
        producto = this.producto.split(",").filter { it.isNotBlank() },
        cantidad = this.cantidad.split(",").filter { it.isNotBlank() },
        precio_total = this.precioTotal,
        fecha = this.fecha,
        estado_pago = this.estado_pago
    )
}



