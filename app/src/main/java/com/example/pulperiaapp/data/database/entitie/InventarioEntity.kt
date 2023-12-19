package com.example.pulperiaapp.data.database.entitie


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("tbl_inventario")
data class InventarioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Int = 0,
    @ColumnInfo("nombreProducto") val nombreProducto: String,
    @ColumnInfo("tamano") val tamano: String,
    @ColumnInfo("fechaEntrega") val fechaEntrega: String,
    @ColumnInfo("fechaEditada") var fechaEditada: String? = null,
    @ColumnInfo("cantidadCajilla") val cantidadCajilla: Int,
    @ColumnInfo("cantidad") val cantidad: Int,
    @ColumnInfo("importe") val precio: Double,
    @ColumnInfo("imagen1") var ruta1: String? = null,
    @ColumnInfo("imagen2") var ruta2: String? = null,
    @ColumnInfo("imagen3") var ruta3: String? = null,

    )

