package com.example.pulperiaapp.domain.inventario

import com.example.pulperiaapp.data.database.entitie.InventarioEntity

data class InventarioModel(
    val id: Int,
    var nombreProducto: String,
    var tamano: String,
    var fechaEntrega: String,
    var fechaEditada: String?,
    var cantidadCajilla: Int,
    var cantidad: Int,
    var importe: Double,
    var imagen1: String? = null,
    var imagen2: String? = null,
    var imagen3: String? = null
)


fun InventarioEntity.toDomain() =
    InventarioModel(
        id = id,
        nombreProducto = nombreProducto,
        tamano = tamano,
        fechaEntrega = fechaEntrega,
        fechaEditada = fechaEditada,
        cantidadCajilla = cantidadCajilla,
        cantidad = cantidad,
        importe = precio,
        imagen1 = ruta1,
        imagen2 = ruta2,
        imagen3 = ruta3
    )
