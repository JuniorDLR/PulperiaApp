package com.example.pulperiaapp.data.repository

import com.example.pulperiaapp.data.database.dao.PrixDao
import com.example.pulperiaapp.data.database.entitie.PrecioPrixEntity
import com.example.pulperiaapp.domain.prix.TablaPrix
import com.example.pulperiaapp.domain.prix.toDomain
import javax.inject.Inject

class TablaPrixRepsitory @Inject constructor(private val prixDao: PrixDao) {

    suspend fun insertarPrixTabla(precioPrixEntity: PrecioPrixEntity) {
        prixDao.insertarPrecioPrix(precioPrixEntity)

    }

    suspend fun obtenerPrixTabla(): List<TablaPrix> {
        val response: List<PrecioPrixEntity> = prixDao.obtenerPrecioPrix()
        return response.map { it.toDomain() }
    }

    suspend fun editarPrixTabla(id: Int, precio: Double) = prixDao.editarPrecioPrix(precio, id)
    suspend fun eliminarPrixTabla(id: Int) = prixDao.eliminarPrecioPrix(id)

    suspend fun obtenerPrecioId(precioId:Int):Double = prixDao.obtenerPrecioId(precioId)

}