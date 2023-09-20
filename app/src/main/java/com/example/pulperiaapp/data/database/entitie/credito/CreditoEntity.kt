package com.example.pulperiaapp.data.database.entitie.credito

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity("tbl_credito")
data class CreditoEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("id") val id: Int = 0,
    @ColumnInfo("fecha") val fecha: Date,
    @ColumnInfo("id_cliente") val id_cliente: Long,//clave foranea
    @ColumnInfo("id_producto") val id_producto: Long, //clave foranea
    @ColumnInfo("cantidad_fiada") val cantidad_fiaja: Int,
    @ColumnInfo("Estado_pago") val estado_pago: Boolean
)