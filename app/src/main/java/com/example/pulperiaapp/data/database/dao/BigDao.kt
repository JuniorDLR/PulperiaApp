package com.example.pulperiaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pulperiaapp.data.database.entitie.PrecioBigCola

@Dao
interface BigDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPrecioBig(precioBigCola: PrecioBigCola)

    @Query("SELECT * FROM tbl_bigcola")
    suspend fun obtenerBigCola(): List<PrecioBigCola>

    @Query("UPDATE tbl_bigcola SET precio=:precio WHERE id=:id")
    suspend fun editarPrecioBig(precio: Double, id: Int)

    @Query("DELETE FROM tbl_bigcola WHERE id=:id ")
    suspend fun eliminarPrecioBig(id: Int)
}