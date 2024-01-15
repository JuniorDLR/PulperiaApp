package com.example.pulperiaapp.domain.amoroso

import com.example.pulperiaapp.data.repository.CreditoRepository
import com.example.pulperiaapp.data.database.entitie.CreditoEntity

import javax.inject.Inject

class UseCaseAmoroso @Inject constructor(private val creditoRepository: CreditoRepository) {

    suspend fun insertarCredito(creditoEntity: MutableList<CreditoEntity>) {
        creditoRepository.guardarCredito(creditoEntity)
    }

    suspend fun eliminarCredito(id: Int) = creditoRepository.eliminarCredito(id)
    suspend fun actualizarpago(nuevoEstado: Boolean, cliente: String) =
        creditoRepository.actualizarPago(nuevoEstado, cliente)

    suspend fun editarCredito(id: Int, producto: String, cantidad: Int, precio: Double,fecha:String) =
        creditoRepository.editarCredito(id,producto, cantidad, precio,fecha)

    suspend fun obtenerDetalleAmoroso(cliente: String): List<VentaAmorosoDetalle> =
        creditoRepository.obtenerDetalleAmoroso(cliente)

    suspend fun obtenerFilterPago(fechaInicio: String,fechaFin:String):List<VentaAmorosoDetalle> = creditoRepository.obtenerFilterPago(fechaInicio,fechaFin)

    suspend fun obtenerCredito(): List<VentaAmorosoDetalle> {
        return creditoRepository.obtenerCredito()
    }


}