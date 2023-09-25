package com.example.pulperiaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.pulperiaapp.data.database.entitie.coca.VentaCocaConPrecio
import com.example.pulperiaapp.data.database.entitie.credito.CreditoClienteProducto
import com.example.pulperiaapp.data.database.entitie.credito.CreditoEntity
import com.example.pulperiaapp.data.database.entitie.prix.VentaPrixConPrecio


@Dao
interface CreditoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCredito(credito: CreditoEntity)

    @Query("SELECT * FROM tbl_cliente")
    suspend fun obtenerNombreCliente():List<CreditoClienteProducto>

    @Transaction
    @Query("SELECT * FROM tbl_precio_coca")
    suspend fun obtenerProductoCoca(): List<VentaCocaConPrecio>

    @Transaction
    @Query("SELECT * FROM tbl_precio_prix")
    suspend fun obtenerProdcutoPrix(): List<VentaPrixConPrecio>

    @Query("SELECT * FROM tbl_credito WHERE id = :id_cliente")
    suspend fun mostrarPorClienteId(id_cliente: Int): CreditoEntity

    @Query("UPDATE tbl_credito SET estado_pago =:nuevoEstado WHERE id =:id")
    suspend fun estadoPago(nuevoEstado: Boolean, id: Int)


}