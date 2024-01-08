package com.example.pulperiaapp.domain.inventario

import com.example.pulperiaapp.data.repository.InventarioRepository
import com.example.pulperiaapp.data.database.entitie.InventarioEntity
import com.example.pulperiaapp.data.database.entitie.InventarioFotoEntity
import javax.inject.Inject

class UseCaseInventario @Inject constructor(private val inventarioRepository: InventarioRepository) {


    suspend fun insertarInventario(inventarioEntity: InventarioEntity) =
        inventarioRepository.insertarInventario(inventarioEntity)

    suspend fun insertarFotos(fotos: List<InventarioFotoEntity>) =
        inventarioRepository.insertarFotos(fotos)

    suspend fun eliminarFoto(idFotos:String) =
        inventarioRepository.eliminarFoto(idFotos)


    suspend fun obtenerFoto(): List<InventarioFotoEntity> = inventarioRepository.obtenerFoto()
    suspend fun obtenerInventarioConFotos(idInventario: String): List<InventarioModel> =
        inventarioRepository.obtenerInventarioConFotos(idInventario)

    suspend fun eliminarInventario(id: Int) = inventarioRepository.eliminarInventario(id)
    suspend fun obtenerInventario():List<InventarioModel> = inventarioRepository.obtenerInventario()


    suspend fun editarInventario(inventarioEntity: InventarioEntity) =
        inventarioRepository.editarInventario(inventarioEntity)
}