package com.example.pulperiaapp.data.database.entitie.cliente

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("tbl_amoroso")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Int = 0,
    @ColumnInfo("nombre") val nombre: String,
)