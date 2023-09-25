package com.example.pulperiaapp.data.database.entitie.coca

import androidx.room.Embedded
import androidx.room.Relation

data class VentaCocaConPrecio(
    @Embedded val ventaCoca: VentaCocaEntity,
    @Relation(
        parentColumn = "producto_coca_id",
        entityColumn = "id"
    )
    val precioCocaEntity: PrecioCocaEntity
)