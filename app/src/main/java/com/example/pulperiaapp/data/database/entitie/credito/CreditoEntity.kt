package com.example.pulperiaapp.data.database.entitie.credito

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity("tbl_credito")
data class CreditoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Int = 0,
    @ColumnInfo("fecha") val fecha: Date,
    @ColumnInfo("cliente_id") val idCliente: Int,//clave foranea
    @ColumnInfo("producto_prix_id") val idProductoPrix: Int, //clave foranea
    @ColumnInfo("producto_coca_id") val idProductoCoca: Int, //clave foranea
    @ColumnInfo("cantidad_fiada") val cantidadFiada: Int,
    @ColumnInfo("precio_total") val precioTotal: Double,
    @ColumnInfo("estado_pago") val estado_pago: Boolean
)