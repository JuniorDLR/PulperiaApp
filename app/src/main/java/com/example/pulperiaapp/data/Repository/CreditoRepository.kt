package com.example.pulperiaapp.data.Repository

import com.example.pulperiaapp.data.database.dao.CreditoDao
import com.example.pulperiaapp.data.database.entitie.CreditoEntity
import com.example.pulperiaapp.domain.amoroso.VentaAmorosoDetalle
import com.example.pulperiaapp.domain.amoroso.toDomain
import javax.inject.Inject

class CreditoRepository @Inject constructor(private val creditoDao: CreditoDao) {


    suspend fun obtenerPagoTotalCliente(cliente: String): List<VentaAmorosoDetalle> {
        val response: List<CreditoEntity> = creditoDao.obtenerPagoTotalCliente(cliente)
        return response.map { it.toDomain() }


    }

    suspend fun guardarCredito(creditoEntity: MutableList<CreditoEntity>) {
        creditoDao.insertarCredito(creditoEntity)
    }

    suspend fun eliminarCredito(cliente: String) {
        creditoDao.eliminarCredito(cliente)
    }

    suspend fun actualizarPago(nuevoEstado: Boolean, cliente: String) =
        creditoDao.estadoPago(nuevoEstado, cliente)

    suspend fun obtenerCredito(): List<VentaAmorosoDetalle> {
        val response: List<CreditoEntity> = creditoDao.obtenerCredito()
        return response.map { it.toDomain() }
    }
}