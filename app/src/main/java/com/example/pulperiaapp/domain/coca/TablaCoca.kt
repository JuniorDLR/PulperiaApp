package com.example.pulperiaapp.domain.coca

import com.example.pulperiaapp.data.database.entitie.coca.PrecioCocaEntity

data class TablaCoca(val id: Int, val producto: String, val precio: Double)

fun PrecioCocaEntity.toDomain() = TablaCoca(id, producto, precio)
