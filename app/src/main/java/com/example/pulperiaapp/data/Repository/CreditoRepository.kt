package com.example.pulperiaapp.data.Repository

import com.example.pulperiaapp.data.database.dao.CreditoDao
import com.example.pulperiaapp.data.database.entitie.CreditoEntity
import com.example.pulperiaapp.domain.amoroso.DetalleAmoroso
import com.example.pulperiaapp.domain.amoroso.VentaAmorosoDetalle
import com.example.pulperiaapp.domain.amoroso.toDomain
import com.example.pulperiaapp.domain.venta.DetalleEditar
import javax.inject.Inject

class CreditoRepository @Inject constructor(private val creditoDao: CreditoDao) {


    suspend fun guardarCredito(creditoEntity: MutableList<CreditoEntity>) {
        creditoDao.insertarCredito(creditoEntity)
    }

    suspend fun eliminarCredito(cliente: String) {
        creditoDao.eliminarCredito(cliente)
    }

    suspend fun editarCredito(id: Int, producto: String, cantidad: Int, precio: Double) =
        creditoDao.editarCredito(id,producto,cantidad, precio)

    suspend fun actualizarPago(nuevoEstado: Boolean, cliente: String) =
        creditoDao.estadoPago(nuevoEstado, cliente)

    suspend fun obtenerDetalleAmoroso(cliente: String): List<VentaAmorosoDetalle> =
        creditoDao.obtenerDetalleAmoroso(cliente)

    suspend fun obtenerCredito(): List<VentaAmorosoDetalle> {
        val response: List<CreditoEntity> = creditoDao.obtenerCredito()
        return response.map { it.toDomain() }
    }

}