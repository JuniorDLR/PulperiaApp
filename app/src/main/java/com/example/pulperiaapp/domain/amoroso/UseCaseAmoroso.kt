package com.example.pulperiaapp.domain.amoroso

import com.example.pulperiaapp.data.Repository.CreditoRepository
import com.example.pulperiaapp.data.database.entitie.credito.CreditoEntity
import javax.inject.Inject

class UseCaseAmoroso @Inject constructor(private val creditoRepository: CreditoRepository) {

    suspend fun insertarCredito(creditoEntity: CreditoEntity) {
        creditoRepository.guardarCredito(creditoEntity)
    }

    suspend fun obtenerCredito():List<VentaAmorosoDetalle>{
        return creditoRepository.obtenerCredito()
    }
}