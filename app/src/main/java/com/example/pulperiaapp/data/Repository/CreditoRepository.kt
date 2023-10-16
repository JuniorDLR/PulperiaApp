package com.example.pulperiaapp.data.Repository

import com.example.pulperiaapp.data.database.dao.CreditoDao
import com.example.pulperiaapp.data.database.entitie.credito.CreditoEntity
import com.example.pulperiaapp.domain.amoroso.VentaAmorosoDetalle
import com.example.pulperiaapp.domain.amoroso.toDomain
import javax.inject.Inject

class CreditoRepository @Inject constructor(private val creditoDao: CreditoDao) {



    suspend fun guardarCredito(creditoEntity: CreditoEntity){
        creditoDao.insertarCredito(creditoEntity)
    }

    suspend fun obtenerCredito():List<VentaAmorosoDetalle>{
        val response:List<CreditoEntity> = creditoDao.obtenerCredito()
        return response.map { it.toDomain()}
    }
}