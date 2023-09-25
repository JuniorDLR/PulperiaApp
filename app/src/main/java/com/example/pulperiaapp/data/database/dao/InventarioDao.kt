package com.example.pulperiaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pulperiaapp.data.database.entitie.inventario.InventarioEntity


@Dao
interface InventarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarInventario(inventarioEntity: InventarioEntity)

    @Query("SELECT * FROM tbl_inventario ORDER BY id")
    suspend fun obtenerInventario(): List<InventarioEntity>
}