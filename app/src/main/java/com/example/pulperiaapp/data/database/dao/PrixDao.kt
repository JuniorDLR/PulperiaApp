package com.example.pulperiaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pulperiaapp.data.database.entitie.prix.PrecioPrixEntity

@Dao
interface PrixDao {



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