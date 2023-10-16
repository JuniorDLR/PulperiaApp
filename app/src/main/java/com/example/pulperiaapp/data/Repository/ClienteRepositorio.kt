package com.example.pulperiaapp.data.Repository

import androidx.room.util.query
import com.example.pulperiaapp.data.database.dao.ClienteDao
import com.example.pulperiaapp.data.database.entitie.cliente.ClienteEntity
import javax.inject.Inject

class ClienteRepositorio @Inject constructor(
    private val clienteDao: ClienteDao
) {
    suspend fun insertarCliente(clienteEntity: ClienteEntity) {
        clienteDao.insertarCliente(clienteEntity)
    }


    suspend fun obtenerProductoPrix(): List<String> {
        return clienteDao.obtenerProductoPrix()
    }

    suspend fun obtenerProductoCoca(): List<String> {
        return clienteDao.obtenerProductoCoca()
    }

    suspend fun obtenerPrecioCoca(producto: String): Double {
        return clienteDao.obtenerPrecioCoca(producto)
    }

    suspend fun obtenerPrecioPrix(producto: String): Double {
        return clienteDao.obtenerPrecioPrix(producto)
    }

    suspend fun obtenerAmoroso(): List<String> {
        return clienteDao.obtenerAmoroso()
    }

}