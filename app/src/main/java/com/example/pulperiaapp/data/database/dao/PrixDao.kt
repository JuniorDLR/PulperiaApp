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

    @Query("SELECT  * FROM tbl_venta_prix ORDER BY id")
    suspend fun obtenerVentapPrix(): List<VentaPrixEntity>

    @Update
    suspend fun editarVentaPrix(prixEntity: VentaPrixEntity)

    @Delete
    suspend fun eliminarVentaPrix(prixEntity: VentaPrixEntity)


    //Precio


    @Query("SELECT * FROM tbl_precio_prix ")
    suspend fun obtenerPrecioPrix(): List<PrecioPrixEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPrecioPrix(precioPrix: PrecioPrixEntity)

    @Query("UPDATE tbl_precio_prix SET precio =:precio WHERE id =:id")
    suspend fun editarPrecioPrix(precio: Double, id: Int)

    @Query("DELETE  FROM  tbl_precio_prix WHERE id =:id")
    suspend fun eliminarPrecioPrix(id: Int)
}