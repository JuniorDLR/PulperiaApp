package com.example.pulperiaapp.data.database.entitie

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_inventario_foto")
data class InventarioFotoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "idFotos") val idFotos: String,
    @ColumnInfo("imagen1") var ruta1: String? = null,
    @ColumnInfo("imagen2") var ruta2: String? = null,
    @ColumnInfo("imagen3") var ruta3: String? = null
)
