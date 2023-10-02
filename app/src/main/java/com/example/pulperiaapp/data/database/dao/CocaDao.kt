package com.example.pulperiaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.pulperiaapp.data.database.entitie.coca.PrecioCocaEntity
import com.example.pulperiaapp.data.database.entitie.coca.VentaCocaConPrecio
import com.example.pulperiaapp.data.database.entitie.coca.VentaCocaEntity

@Dao
interface CocaDao {

    //venta


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVentaCoca(ventaCoca: VentaCocaEntity)

    @Query("SELECT * FROM tbl_venta_coca ORDER BY id")
    suspend fun obtenerVentaCoca(): List<VentaCocaEntity>

    @Delete
    suspend fun eliminarVentaCoca(ventaCoca: VentaCocaEntity)

    @Update
    suspend fun editarVentaCoca(ventaCoca: VentaCocaEntity)


    //precio


    @Query("SELECT * FROM tbl_precio_coca")
    suspend fun obtenerPrecioCoca(): List<PrecioCocaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPrecioCoca(precioCoca: PrecioCocaEntity)

    @Query("UPDATE tbl_precio_coca SET precio=:precio WHERE id =:id")
    suspend fun editarPrecioCoca(id: Int, precio: Double)

    @Query("DELETE  FROM  tbl_precio_coca WHERE id =:id")
    suspend fun eliminarPrecioCoca(id: Int)


}