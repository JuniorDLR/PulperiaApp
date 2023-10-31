package com.example.pulperiaapp.data.Repository

import com.example.pulperiaapp.data.database.dao.BigDao
import com.example.pulperiaapp.data.database.entitie.PrecioBigCola
import com.example.pulperiaapp.domain.bigcola.TablaBig
import com.example.pulperiaapp.domain.bigcola.toDomain
import javax.inject.Inject


class TablaBigRepositorio @Inject constructor(private val bigDao: BigDao) {

    suspend fun insertarPrecioBig(precioBigCola: PrecioBigCola) =
        bigDao.insertarPrecioBig(precioBigCola)

    suspend fun obtenerBigCola(): List<TablaBig> {
        val response: List<PrecioBigCola> = bigDao.obtenerBigCola()
        return response.map { it.toDomain() }
    }

    suspend fun editarBigCola(precio: Double, id: Int) = bigDao.editarPrecioBig(precio, id)
    suspend fun eliminarBigCola(id: Int) = bigDao.eliminarPrecioBig(id)


}