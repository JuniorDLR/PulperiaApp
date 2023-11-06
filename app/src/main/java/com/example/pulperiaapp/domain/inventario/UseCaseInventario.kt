package com.example.pulperiaapp.domain.inventario

import com.example.pulperiaapp.data.Repository.InventarioRepository
import com.example.pulperiaapp.data.database.entitie.InventarioEntity
import javax.inject.Inject

class UseCaseInventario @Inject constructor(private val inventarioRepository: InventarioRepository) {


    suspend fun insertarInventario(inventarioEntity: MutableList<InventarioEntity>) =
        inventarioRepository.insertarInventario(inventarioEntity)

    suspend fun obtenerInventario(): List<InventarioModel> =
        inventarioRepository.obtenerInventario()

    suspend fun eliminarInventario(id: Int) = inventarioRepository.eliminarInventario(id)
}