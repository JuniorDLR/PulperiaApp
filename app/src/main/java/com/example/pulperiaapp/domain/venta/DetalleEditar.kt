package com.example.pulperiaapp.domain.venta

data class DetalleEditar(
    val id: Int,
    val producto: String,
    var cantidad: Int,
    var totalVenta: Double,
    var ventaPorCajilla: Boolean

)
