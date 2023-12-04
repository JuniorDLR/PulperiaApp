package com.example.pulperiaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pulperiaapp.data.database.entitie.InventarioEntity
import com.example.pulperiaapp.domain.inventario.InventarioModel


@Dao
interface InventarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarInventario(inventarioEntity: InventarioEntity)

    @Query("SELECT * FROM tbl_inventario ORDER BY id")
    suspend fun obtenerInventario(): List<InventarioEntity>

    @Query("DELETE FROM tbl_inventario WHERE id=:id")
    suspend fun eliminarInventario(id: Int)

    @Query("SELECT * FROM tbl_inventario WHERE fecha_entrega =:idInventario")
    suspend fun obtenerDetalleInventario(idInventario: String): List<InventarioModel>

    @Update
    suspend fun editarInventario(inventarioEntity: InventarioEntity)


}