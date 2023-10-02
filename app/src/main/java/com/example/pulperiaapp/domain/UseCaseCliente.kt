package com.example.pulperiaapp.domain

import com.example.pulperiaapp.data.Repository.ClienteRepositorio
import com.example.pulperiaapp.data.database.entitie.cliente.ClienteEntity
import javax.inject.Inject


class UseCaseCliente @Inject constructor(private val clienteRepositorio: ClienteRepositorio) {

    suspend fun insertarCliente(cliente: ClienteEntity) {
        clienteRepositorio.insertarCliente(cliente)
    }
}