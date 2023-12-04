package com.example.pulperiaapp.domain.venta

data class DetalleEditar(
    val id: Int,
    val producto: String,
    var cantidad: Int,
    var total_venta: Double,
    var venta_por_cajilla: Boolean

)
