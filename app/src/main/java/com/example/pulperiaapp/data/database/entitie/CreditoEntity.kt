package com.example.pulperiaapp.data.database.entitie

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("tbl_credito")
data class CreditoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Int = 0,
    @ColumnInfo("cliente") val cliente: String,
    @ColumnInfo("producto") val producto: String,
    @ColumnInfo("cantidad") val cantidad: Int,
    @ColumnInfo("precioTotal") val precioTotal: Double,
    @ColumnInfo("fecha") val fecha: String,
    @ColumnInfo("estadoPago") var estadoPago: Boolean
)



