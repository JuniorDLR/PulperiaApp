package com.example.pulperiaapp.data.database.entitie.coca

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity("tbl_venta_coca")
data class VentaCocaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Int = 0,
    @ColumnInfo("fecha_venta") val fecha: Date,
    @ColumnInfo("cantidad_vendida") val cantidad: Int,
    @ColumnInfo("producto_coca_id") val id_producto: Long, // Clave foránea que se relaciona con Producto
    @ColumnInfo("precio_unitario") val precio_unitario: Double,
    @ColumnInfo("total_venta") val total_venta: Double
)