package com.example.pulperiaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pulperiaapp.data.database.entitie.CreditoEntity


@Dao
interface CreditoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCredito(credito: MutableList<CreditoEntity>)


    @Query("SELECT * FROM tbl_credito")
    suspend fun obtenerCredito(): List<CreditoEntity>

    @Query("DELETE FROM tbl_credito WHERE cliente =:cliente")
    suspend fun eliminarCredito(cliente: String)


    @Query("UPDATE tbl_credito SET estado_pago =:nuevoEstado WHERE cliente =:cliente")
    suspend fun estadoPago(nuevoEstado: Boolean, cliente: String)

    @Query("SELECT id,cliente,producto,SUM(cantidad) AS cantidad,SUM(precio_total) AS precio_total,fecha,estado_pago  FROM tbl_credito WHERE cliente=:nombre GROUP BY cliente")
    suspend fun obtenerPagoTotalCliente(nombre: String): List<CreditoEntity>


}