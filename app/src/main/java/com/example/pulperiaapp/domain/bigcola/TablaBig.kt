package com.example.pulperiaapp.domain.bigcola

import com.example.pulperiaapp.data.database.entitie.PrecioBigCola

data class TablaBig(val id: Int, val producto: String, val precio: Double)

fun PrecioBigCola.toDomain() = TablaBig(id = id, producto = producto, precio = precio)