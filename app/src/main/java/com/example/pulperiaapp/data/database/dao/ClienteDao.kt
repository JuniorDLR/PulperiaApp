package com.example.pulperiaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pulperiaapp.data.database.entitie.cliente.ClienteEntity

@Dao
interface ClienteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCliente(cliente: ClienteEntity)


    @Query("SELECT producto FROM tbl_precio_prix")
    suspend fun obtenerProductoPrix(): List<String>

    @Query("SELECT producto FROM tbl_precio_coca")
    suspend fun obtenerProductoCoca(): List<String>

    @Query("SELECT precio FROM tbl_precio_coca WHERE producto=:producto")
    suspend fun obtenerPrecioCoca(producto: String): Double

    @Query("SELECT precio FROM tbl_precio_prix WHERE producto=:producto")
    suspend fun obtenerPrecioPrix(producto: String): Double

    @Query("SELECT nombre FROM tbl_cliente")
    suspend fun obtenerAmoroso(): List<String>


}