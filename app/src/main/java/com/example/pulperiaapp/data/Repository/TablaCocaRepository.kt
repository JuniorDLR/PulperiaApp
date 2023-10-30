package com.example.pulperiaapp.data.Repository

import com.example.pulperiaapp.data.database.dao.CocaDao
import com.example.pulperiaapp.data.database.entitie.PrecioCocaEntity
import com.example.pulperiaapp.domain.coca.TablaCoca

import com.example.pulperiaapp.domain.coca.toDomain
import javax.inject.Inject

class TablaCocaRepository @Inject constructor(private val cocaDao: CocaDao) {

    suspend fun insertarCocaTabla(precioCocaEntity: PrecioCocaEntity) =
        cocaDao.insertarPrecioCoca(precioCocaEntity)


    suspend fun obtenerCocaTabla(): List<TablaCoca> {
        val response: List<PrecioCocaEntity> = cocaDao.obtenerPrecioCoca()
        return response.map { it.toDomain() }
    }

    suspend fun editarCocaTabla(id: Int, precio: Double) = cocaDao.editarPrecioCoca(id, precio)
    suspend fun eliminarProducto(id: Int) = cocaDao.eliminarPrecioCoca(id)

}