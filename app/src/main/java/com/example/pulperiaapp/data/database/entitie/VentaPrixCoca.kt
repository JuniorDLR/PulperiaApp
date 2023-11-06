package com.example.pulperiaapp.data.database.entitie

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("tbl_venta_prix_coca")
data class VentaPrixCoca(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Int = 0,
    @ColumnInfo("producto") val producto: String,
    @ColumnInfo("total_venta") val total: Double,
    @ColumnInfo("fecha_venta") val fecha: Long,
    @ColumnInfo("venta_por_cajilla") val ventaPorCajilla: Boolean,
    @ColumnInfo("cantidad") val cantidad: Int
)




