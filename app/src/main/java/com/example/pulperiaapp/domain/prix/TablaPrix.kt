package com.example.pulperiaapp.domain.prix

import com.example.pulperiaapp.data.database.entitie.prix.PrecioPrixEntity

data class TablaPrix(val id: Int, val producto: String, val precio: Double)

fun PrecioPrixEntity.toDomain() = TablaPrix(id, producto, precio)
