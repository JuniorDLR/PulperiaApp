package com.example.pulperiaapp.data.Repository

import com.example.pulperiaapp.data.database.dao.ClienteDao
import com.example.pulperiaapp.data.database.entitie.cliente.ClienteEntity
import javax.inject.Inject

class ClienteRepositorio @Inject constructor(
    private val clienteDao: ClienteDao
) {


    suspend fun insertarCliente(clienteEntity: ClienteEntity) {
        clienteDao.insertarCliente(clienteEntity)

    }

}