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

    @Query("UPDATE tbl_credito SET producto=:producto, cantidad=:cantidad, precio_total=:precio,fecha=:fecha WHERE id=:id")
    suspend fun editarCredito(
        id: Int,
        producto: String,
        cantidad: Int,
        precio: Double,
        fecha: String
    )


    @Query("UPDATE tbl_credito SET estado_pago =:nuevoEstado WHERE cliente =:cliente")
    suspend fun estadoPago(nuevoEstado: Boolean, cliente: String)

    @Query("SELECT * FROM tbl_credito WHERE cliente = :cliente AND estado_pago = 0")
    suspend fun obtenerDetalleAmoroso(cliente: String): List<VentaAmorosoDetalle>

    @Query("SELECT * FROM tbl_credito WHERE  estado_pago = 1")
    suspend fun obtenerFilterPago(): List<VentaAmorosoDetalle>


}