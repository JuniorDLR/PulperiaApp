package com.example.pulperiaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pulperiaapp.data.database.entitie.InventarioEntity
import com.example.pulperiaapp.data.database.entitie.InventarioEntityConFotos
import com.example.pulperiaapp.data.database.entitie.InventarioFotoEntity

@Dao
interface InventarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarInventario(inventarioEntity: InventarioEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarFotos(fotos: List<InventarioFotoEntity>)

    @Query("SELECT  *  FROM tbl_inventario_foto")
    suspend fun obtenerFoto(): List<InventarioFotoEntity>

    @Query("DELETE  FROM tbl_inventario_foto WHERE idFotos =:idFotos")
    suspend fun eliminarFoto(idFotos:String)



    @Query("SELECT * FROM tbl_inventario WHERE fechaEntrega =:idInventario")
    suspend fun obtenerInventarioConFotos( idInventario: String): List<InventarioEntityConFotos>

    @Query("SELECT * FROM tbl_inventario")
    suspend fun obtenerInventario(): List<InventarioEntity>
    @Query("DELETE FROM tbl_inventario WHERE id=:id")
    suspend fun eliminarInventario(id: Int)



    @Update
    suspend fun editarInventario(inventarioEntity: InventarioEntity)
}
