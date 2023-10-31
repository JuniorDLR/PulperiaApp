package com.example.pulperiaapp.data.database.dao

import androidx.room.Dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.example.pulperiaapp.data.database.entitie.VentaPrixCoca

import com.example.pulperiaapp.ui.view.venta.viewmodel.VentaPrixCocaDetalle

@Dao
interface VentaCocaPrix {


    //venta

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertatVenta(ventaPrixCoca: VentaPrixCoca)

    @Query("SELECT id,producto,total_venta,fecha_venta,cantidad FROM tbl_venta_prix_coca")
    suspend fun obtenerVenta(): List<VentaPrixCocaDetalle>


    @Query("SELECT producto FROM tbl_precio_prix")
    suspend fun obtenerProductoPrix(): List<String>

    @Query("SELECT producto FROM tbl_precio_coca")
    suspend fun obtenerProdcutoCoca(): List<String>

    @Query("SELECT producto FROM tbl_bigcola")
    suspend fun obtenerProdcutoBig(): List<String>


    @Query("DELETE  FROM  tbl_venta_prix_coca WHERE id =:id")
    suspend fun eliminarVenta(id: Int)

    @Query("SELECT precio FROM tbl_precio_prix WHERE producto =:producto")
    suspend fun obtenerPrecioPrix(producto: String): Double


    @Query("SELECT precio FROM tbl_precio_coca WHERE producto =:producto")
    suspend fun obtenerPrecioCoca(producto: String): Double

    @Query("SELECT precio FROM tbl_bigcola WHERE producto=:producto")
    suspend fun obtenerPrecioBig(producto: String): Double

}