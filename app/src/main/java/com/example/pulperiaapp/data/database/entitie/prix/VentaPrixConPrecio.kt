package com.example.pulperiaapp.data.database.entitie.prix

import androidx.room.Embedded
import androidx.room.Relation
import com.example.pulperiaapp.data.database.entitie.prix.PrecioPrixEntity
import com.example.pulperiaapp.data.database.entitie.prix.VentaPrixEntity

data class VentaPrixConPrecio(

    //se usa para incluir la información de la venta Prix Cola
    @Embedded val ventaPrix: VentaPrixEntity,
    @Relation(
        parentColumn = "id_producto",
        entityColumn = "id"
    )
    val precioPrix: PrecioPrixEntity
)