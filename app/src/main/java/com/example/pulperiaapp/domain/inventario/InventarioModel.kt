package com.example.pulperiaapp.domain.inventario

import com.example.pulperiaapp.data.database.entitie.InventarioEntity

data class InventarioModel(
    val id: Int,
    val nombre: String,
    val tamano: String,
    val fecha_entrega: String,
    val cantidad_cajilla: String,
    val cantidad: Int,
    val importe: Double,
    val ruta1: String? = null,
    val ruta2: String? = null,
    val ruta3: String? = null
)


fun InventarioEntity.toDomain() =
    InventarioModel(
        id = id,
        nombre = nombreProducto,
        tamano = tamano,
        fecha_entrega = fechaEntrega,
        cantidad_cajilla = cantidadCajilla,
        cantidad = cantidad,
        importe = precio,
        ruta1 = ruta1,
        ruta2 = ruta2,
        ruta3 = ruta3

    )
