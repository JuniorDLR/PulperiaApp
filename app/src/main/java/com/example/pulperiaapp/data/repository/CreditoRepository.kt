package com.example.pulperiaapp.data.repository

import com.example.pulperiaapp.data.database.dao.CreditoDao
import com.example.pulperiaapp.data.database.entitie.CreditoEntity
import com.example.pulperiaapp.domain.amoroso.VentaAmorosoDetalle
import com.example.pulperiaapp.domain.amoroso.toDomain
import javax.inject.Inject

class CreditoRepository @Inject constructor(private val creditoDao: CreditoDao) {


    suspend fun guardarCredito(creditoEntity: MutableList<CreditoEntity>) {
        creditoDao.insertarCredito(creditoEntity)
    }

    suspend fun eliminarCredito(id: Int) {
        creditoDao.eliminarCredito(id)
    }

    suspend fun editarCredito(id: Int, producto: String, cantidad: Int, precio: Double,fecha:String) =
        creditoDao.editarCredito(id,producto,cantidad, precio,fecha)

    suspend fun actualizarPago(nuevoEstado: Boolean, cliente: String) =
        creditoDao.estadoPago(nuevoEstado, cliente)

    suspend fun obtenerDetalleAmoroso(cliente: String): List<VentaAmorosoDetalle> =
        creditoDao.obtenerDetalleAmoroso(cliente)

    suspend fun obtenerFilterPago():List<VentaAmorosoDetalle> =creditoDao.obtenerFilterPago()


    suspend fun obtenerCredito(): List<VentaAmorosoDetalle> {
        val response: List<CreditoEntity> = creditoDao.obtenerCredito()
        return response.map { it.toDomain() }
    }

}