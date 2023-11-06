package com.example.pulperiaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pulperiaapp.data.database.entitie.InventarioEntity


@Dao
interface InventarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarInventario(inventarioEntity: MutableList<InventarioEntity>)

    @Query("SELECT * FROM tbl_inventario ORDER BY id")
    suspend fun obtenerInventario(): List<InventarioEntity>

    @Query("DELETE FROM tbl_inventario WHERE id=:id")
    suspend fun eliminarInventario(id: Int)


}