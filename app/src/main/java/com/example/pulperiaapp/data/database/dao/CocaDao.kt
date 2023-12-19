package com.example.pulperiaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pulperiaapp.data.database.entitie.PrecioCocaEntity

@Dao
interface CocaDao {

    @Query("SELECT * FROM tbl_precio_coca")
    suspend fun obtenerPrecioCoca(): List<PrecioCocaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPrecioCoca(precioCoca: PrecioCocaEntity)

    @Query("UPDATE tbl_precio_coca SET precio=:precio WHERE id =:id")
    suspend fun editarPrecioCoca(id: Int, precio: Double)

    @Query("DELETE  FROM  tbl_precio_coca WHERE id =:id")
    suspend fun eliminarPrecioCoca(id: Int)


    @Query("SELECT precio  FROM tbl_precio_coca WHERE id=:idPrecio")
    suspend fun obtenerPrecioId(idPrecio:Int):Double


}