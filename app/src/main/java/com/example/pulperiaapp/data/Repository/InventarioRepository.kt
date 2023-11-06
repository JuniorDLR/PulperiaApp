package com.example.pulperiaapp.data.Repository

import com.example.pulperiaapp.data.database.dao.InventarioDao
import com.example.pulperiaapp.data.database.entitie.InventarioEntity
import com.example.pulperiaapp.domain.inventario.InventarioModel
import com.example.pulperiaapp.domain.inventario.toDomain
import javax.inject.Inject

class InventarioRepository @Inject constructor(private val inventarioDao: InventarioDao) {


    suspend fun insertarInventario(inventarioEntity: MutableList<InventarioEntity>) =
        inventarioDao.insertarInventario(inventarioEntity)

    suspend fun obtenerInventario(): List<InventarioModel> {
        val response: List<InventarioEntity> = inventarioDao.obtenerInventario()
        return response.map { it.toDomain() }
    }

    suspend fun eliminarInventario(id: Int) = inventarioDao.eliminarInventario(id)
}