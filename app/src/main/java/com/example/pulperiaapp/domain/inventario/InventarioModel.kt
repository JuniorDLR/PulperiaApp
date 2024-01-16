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
    var idFotos: String?
)


fun InventarioEntity.toDomain() = InventarioModel(
    id = id,
    nombreProducto = nombreProducto,
    tamano = tamano,
    fechaEntrega = fechaEntrega,
    fechaEditada = fechaEditada,
    cantidadCajilla = cantidadCajilla,
    cantidad = cantidad,
    importe = precio,
    idFotos = idFotos
)
