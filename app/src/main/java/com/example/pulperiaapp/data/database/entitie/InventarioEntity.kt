package com.example.pulperiaapp.data.database.entitie


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tbl_inventario")
data class InventarioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "nombreProducto") val nombreProducto: String,
    @ColumnInfo(name = "tamano") val tamano: String,
    @ColumnInfo(name = "fechaEntrega") val fechaEntrega: String,
    @ColumnInfo(name = "fechaEditada") var fechaEditada: String? = null,
    @ColumnInfo(name = "cantidadCajilla") val cantidadCajilla: Int,
    @ColumnInfo(name = "cantidad") val cantidad: Int,
    @ColumnInfo(name = "importe") val precio: Double,
    @ColumnInfo(name = "idFotos") val idFotos: String? = null
)


