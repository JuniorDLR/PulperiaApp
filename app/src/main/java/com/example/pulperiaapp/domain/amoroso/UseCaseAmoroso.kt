package com.example.pulperiaapp.domain.amoroso

import com.example.pulperiaapp.data.Repository.CreditoRepository
import com.example.pulperiaapp.data.database.entitie.CreditoEntity
import com.example.pulperiaapp.domain.venta.DetalleEditar
import javax.inject.Inject

class UseCaseAmoroso @Inject constructor(private val creditoRepository: CreditoRepository) {

    suspend fun insertarCredito(creditoEntity: MutableList<CreditoEntity>) {
        creditoRepository.guardarCredito(creditoEntity)
    }

    suspend fun eliminarCredito(cliente: String) = creditoRepository.eliminarCredito(cliente)
    suspend fun actualizarpago(nuevoEstado: Boolean, cliente: String) =
        creditoRepository.actualizarPago(nuevoEstado, cliente)

    suspend fun editarCredito(id: Int, producto: String, cantidad: Int, precio: Double) =
        creditoRepository.editarCredito(id,producto, cantidad, precio)

    suspend fun obtenerDetalleAmoroso(cliente: String): List<VentaAmorosoDetalle> =
        creditoRepository.obtenerDetalleAmoroso(cliente)

    suspend fun obtenerCredito(): List<VentaAmorosoDetalle> {
        return creditoRepository.obtenerCredito()
    }


}