package com.example.pulperiaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pulperiaapp.data.database.entitie.coca.VentaCocaEntity
import com.example.pulperiaapp.data.database.entitie.prix.PrecioPrixEntity
import com.example.pulperiaapp.data.database.entitie.prix.VentaPrixEntity

@Dao
interface PrixDao {


   //venta

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertatVentaPrix(ventaPrix: VentaPrixEntity)

    @Query("SELECT  * FROM tbl_venta_coca ORDER BY id")
    suspend fun obtenerVentaCoca():List<VentaCocaEntity>

    @Update
    suspend fun editarVentaCoca(ventaCoca:VentaCocaEntity)

    @Delete
    suspend fun eliminarVentaCoca(ventaCoca: VentaCocaEntity)


    //Precio


    @Query("SELECT * FROM tbl_precio_prix ORDER BY id")
    suspend fun obtenerPrecioPrix(): List<PrecioPrixEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPrecioPrix(precioPrix: PrecioPrixEntity)

    @Update
    suspend fun editarPrecioPrix(precioPrix: PrecioPrixEntity)

    @Query("DELETE  FROM  tbl_precio_prix WHERE id =:id")
    suspend fun eliminarPrecioPrix(id: Int)
}