package com.example.pulperiaapp.domain.amoroso

data class DetalleAmoroso(
    val id: Int,
    val producto: String,
    var cantidad: Int,
    var precioTotal: Double,

    )