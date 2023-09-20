package com.example.pulperiaapp.data.database.entitie

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("tbl_usuario")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Int = 0,
    @ColumnInfo("usuario") val usuario: String,
    @ColumnInfo("contrasena") val pw: String
)