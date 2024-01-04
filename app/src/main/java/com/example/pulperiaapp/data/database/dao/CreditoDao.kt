package com.example.pulperiaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pulperiaapp.data.database.entitie.CreditoEntity
import com.example.pulperiaapp.domain.amoroso.VentaAmorosoDetalle


@Dao
interface CreditoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCredito(credito: MutableList<CreditoEntity>)


    @Query("SELECT * FROM tbl_credito")
    suspend fun obtenerCredito(): List<CreditoEntity>

    @Query("DELETE FROM tbl_credito WHERE id =:id")
    suspend fun eliminarCredito(id: Int)

    @Query("UPDATE tbl_credito SET producto=:producto, cantidad=:cantidad, precioTotal=:precio,fecha=:fecha WHERE id=:id")
    suspend fun editarCredito(
        id: Int,
        producto: String,
        cantidad: Int,
        precio: Double,
        fecha: String
    )


    @Query("UPDATE tbl_credito SET estadoPago =:nuevoEstado WHERE cliente =:cliente")
    suspend fun estadoPago(nuevoEstado: Boolean, cliente: String)

    @Query("SELECT * FROM tbl_credito WHERE cliente = :cliente AND estadoPago = 0")
    suspend fun obtenerDetalleAmoroso(cliente: String): List<VentaAmorosoDetalle>

    @Query("SELECT * FROM tbl_credito WHERE  estadoPago = 1 AND DATE(fecha)>=DATE(:fechaFilter)")
    suspend fun obtenerFilterPago(fechaFilter: String): List<VentaAmorosoDetalle>


}