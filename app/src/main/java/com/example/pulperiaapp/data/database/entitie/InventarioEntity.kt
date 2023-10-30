package com.example.pulperiaapp.data.database.entitie


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("tbl_inventario")
data class InventarioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Int = 0,
    @ColumnInfo("fecha_entrega") val fechaEntrega: String,
    @ColumnInfo("nombre_producto") val nombreProducto: String,
    @ColumnInfo("pago_total") val pagoTotal: Double,
    @ColumnInfo("imagen") val imagen:String
)
