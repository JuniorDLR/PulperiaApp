package com.example.pulperiaapp.data.database.entitie

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("tbl_venta_prix_coca")
data class VentaPrixCoca(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") var id: Int = 0,
    @ColumnInfo("producto") val producto: String,
    @ColumnInfo("totalVenta") val total: Double,
    @ColumnInfo("fechaVenta") val fecha: String,
    @ColumnInfo("fechaEditada") val fechaEditada: String? = null,
    @ColumnInfo("ventaPorCajilla") val ventaPorCajilla: Boolean,
    @ColumnInfo("cantidad") val cantidad: Int)




