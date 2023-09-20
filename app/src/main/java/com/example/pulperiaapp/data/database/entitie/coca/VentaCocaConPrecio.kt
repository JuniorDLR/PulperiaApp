package com.example.pulperiaapp.data.database.entitie.coca

import androidx.room.Embedded
import androidx.room.Relation

data class VentaCocaConPrecio(
    @Embedded val ventaCoca: VentaCocaEntity,
    @Relation(
        parentColumn = "id_prodcuto",
        entityColumn = "id"
    )
    val precioCocaEntity: PrecioCocaEntity
)