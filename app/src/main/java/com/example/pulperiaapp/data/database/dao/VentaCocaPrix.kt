package com.example.pulperiaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pulperiaapp.data.database.entitie.VentaPrixCoca
import com.example.pulperiaapp.domain.venta.DetalleEditar

@Dao
interface VentaCocaPrix {


    //venta

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertatVenta(ventaPrixCoca: VentaPrixCoca)

    @Query("SELECT SUM(total_venta) FROM tbl_venta_prix_coca")
    suspend fun obtenerTotal(): Double

    @Query("SELECT *FROM tbl_venta_prix_coca")
    suspend fun obtenerVenta(): List<VentaPrixCoca>

   @Update
    suspend fun editarVenta(ventaPrixCoca: VentaPrixCoca)


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

    @Query("SELECT producto,cantidad,total_venta FROM tbl_venta_prix_coca WHERE id =:id")
    suspend fun obtenerDetalleEditar(id: Int): List<DetalleEditar>


}