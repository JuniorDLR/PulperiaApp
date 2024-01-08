package com.example.pulperiaapp.data.repository

import com.example.pulperiaapp.data.database.dao.InventarioDao
import com.example.pulperiaapp.data.database.entitie.InventarioEntity
import com.example.pulperiaapp.data.database.entitie.InventarioFotoEntity
import com.example.pulperiaapp.domain.inventario.InventarioModel
import com.example.pulperiaapp.domain.inventario.toDomain
import javax.inject.Inject

class InventarioRepository @Inject constructor(private val inventarioDao: InventarioDao) {


    suspend fun insertarInventario(inventarioEntity: InventarioEntity) =
        inventarioDao.insertarInventario(inventarioEntity)

    suspend fun insertarFotos(fotos: List<InventarioFotoEntity>) =
        inventarioDao.insertarFotos(fotos)

    suspend fun eliminarFoto(idFotos: String) =
        inventarioDao.eliminarFoto(idFotos)

    suspend fun obtenerFoto(): List<InventarioFotoEntity> = inventarioDao.obtenerFoto()

    suspend fun obtenerInventarioConFotos(idInventario: String): List<InventarioModel> {
        val inventarioConFotos = inventarioDao.obtenerInventarioConFotos(idInventario)
        return inventarioConFotos.map {
            InventarioModel(
                id = it.inventario.id,
                nombreProducto = it.inventario.nombreProducto,
                tamano = it.inventario.tamano,
                fechaEntrega = it.inventario.fechaEntrega,
                fechaEditada = it.inventario.fechaEditada,
                cantidadCajilla = it.inventario.cantidadCajilla,
                cantidad = it.inventario.cantidad,
                importe = it.inventario.precio,
                idFotos = it.inventario.idFotos

            )
        }
    }


    suspend fun eliminarInventario(id: Int) = inventarioDao.eliminarInventario(id)
    suspend fun obtenerInventario(): List<InventarioModel> {
        val response: List<InventarioEntity> = inventarioDao.obtenerInventario()
        return response.map { it.toDomain() }
    }


    suspend fun editarInventario(inventarioEntity: InventarioEntity) =
        inventarioDao.editarInventario(inventarioEntity)
}