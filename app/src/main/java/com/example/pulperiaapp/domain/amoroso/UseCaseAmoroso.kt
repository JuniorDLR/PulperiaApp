package com.example.pulperiaapp.domain.amoroso

import com.example.pulperiaapp.data.Repository.CreditoRepository
import com.example.pulperiaapp.data.database.entitie.CreditoEntity
import javax.inject.Inject

class UseCaseAmoroso @Inject constructor(private val creditoRepository: CreditoRepository) {

    suspend fun insertarCredito(creditoEntity: MutableList<CreditoEntity>) {
        creditoRepository.guardarCredito(creditoEntity)
    }

    suspend fun eliminarCredito(cliente: String) = creditoRepository.eliminarCredito(cliente)
    suspend fun actualizarpago(nuevoEstado: Boolean, cliente: String) =
        creditoRepository.actualizarPago(nuevoEstado, cliente)


    suspend fun obtenerPagoTotalCliente(cliente: String): List<VentaAmorosoDetalle> {
        return creditoRepository.obtenerPagoTotalCliente(cliente)
    }

    suspend fun obtenerCredito(): List<VentaAmorosoDetalle> {
        return creditoRepository.obtenerCredito()
    }
}