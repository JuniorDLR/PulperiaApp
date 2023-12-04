package com.example.pulperiaapp.domain.inventario

import com.example.pulperiaapp.data.database.entitie.InventarioEntity

data class InventarioModel(
    val id: Int,
    var nombre_producto: String,
    var tamano: String,
    var fecha_entrega: String,
    var fecha_editada: String?,
    var cantidad_cajilla: Int,
    var cantidad: Int,
    var importe: Double,
    var imagen1: String? = null,
    var imagen2: String? = null,
    var imagen3: String? = null
)


fun InventarioEntity.toDomain() =
    InventarioModel(
        id = id,
        nombre_producto = nombreProducto,
        tamano = tamano,
        fecha_entrega = fechaEntrega,
        fecha_editada = fechaEditada,
        cantidad_cajilla = cantidadCajilla,
        cantidad = cantidad,
        importe = precio,
        imagen1 = ruta1,
        imagen2 = ruta2,
        imagen3 = ruta3
    )
