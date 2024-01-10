package com.example.pulperiaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pulperiaapp.data.database.entitie.UsuarioEntity

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarUsuario(usuarioEntity: UsuarioEntity)

    @Query("SELECT * FROM tbl_usuario ORDER BY id")
    suspend fun obtenerUsuario():List<UsuarioEntity>

    @Query("UPDATE tbl_usuario SET contrasena=:pw WHERE id =:idUsuario")
    suspend fun editarUsuario(pw:String,idUsuario:Int)
}