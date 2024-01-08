package com.example.pulperiaapp.data.database.entitie

import androidx.room.Embedded
import androidx.room.Relation


data class InventarioEntityConFotos(
    @Embedded val inventario: InventarioEntity,
    @Relation(
        parentColumn = "idFotos",
        entityColumn = "idFotos"
    )
    val fotos: List<InventarioFotoEntity>
)
