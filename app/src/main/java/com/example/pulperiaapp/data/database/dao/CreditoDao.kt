package com.example.pulperiaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pulperiaapp.data.database.entitie.credito.CreditoEntity
import com.example.pulperiaapp.domain.amoroso.VentaAmorosoDetalle


@Dao
interface CreditoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCredito(credito: CreditoEntity)


    @Query("SELECT * FROM tbl_credito")
    suspend fun obtenerCredito(): List<CreditoEntity>


    @Query("UPDATE tbl_credito SET estado_pago =:nuevoEstado WHERE id =:id")
    suspend fun estadoPago(nuevoEstado: Boolean, id: Int)


}