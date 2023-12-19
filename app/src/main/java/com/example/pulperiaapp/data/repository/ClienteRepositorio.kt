package com.example.pulperiaapp.data.repository

import com.example.pulperiaapp.data.database.dao.ClienteDao
import com.example.pulperiaapp.data.database.entitie.ClienteEntity
import javax.inject.Inject

class ClienteRepositorio @Inject constructor(
    private val clienteDao: ClienteDao
) {
    suspend fun insertarCliente(clienteEntity: ClienteEntity) {
        clienteDao.insertarCliente(clienteEntity)
    }

    suspend fun obtenerProductoBig(): List<String> {
        return clienteDao.obtenerProductoBig()
    }

    suspend fun obtenerPrecioBig(producto: String): Double {
        return clienteDao.obtenerPrecioBig(producto)
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