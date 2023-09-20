package com.example.pulperiaapp.data.database.entitie.credito

import androidx.room.Embedded
import androidx.room.Relation
import com.example.pulperiaapp.data.database.entitie.cliente.ClienteEntity
import com.example.pulperiaapp.data.database.entitie.coca.PrecioCocaEntity
import com.example.pulperiaapp.data.database.entitie.prix.PrecioPrixEntity

data class CreditoClienteProducto(
    @Embedded val creditoEntity: CreditoEntity,

    @Relation(
        parentColumn = "id_cliente",
        entityColumn = "id"
    )
    val cliente: ClienteEntity,
    @Relation(
        parentColumn = "id_producto",
        entityColumn = "id",
        entity = PrecioPrixEntity::class
    )
    val precioPrix: PrecioPrixEntity?,

    @Relation(
        parentColumn = "id_producto",
        entityColumn = "id",
        entity = PrecioCocaEntity::class
    )
    val precioCoca: PrecioCocaEntity

)