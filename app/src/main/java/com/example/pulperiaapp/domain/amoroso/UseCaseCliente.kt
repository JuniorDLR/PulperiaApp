package com.example.pulperiaapp.domain.amoroso

import com.example.pulperiaapp.data.Repository.ClienteRepositorio
import com.example.pulperiaapp.data.database.entitie.ClienteEntity
import javax.inject.Inject


class UseCaseCliente @Inject constructor(private val clienteRepositorio: ClienteRepositorio) {

    suspend fun insertarCliente(cliente: ClienteEntity) = clienteRepositorio.insertarCliente(cliente)
    suspend fun obtenerProductoPrix(): List<String> = clienteRepositorio.obtenerProductoPrix()
    suspend fun obtenerProductoCoca(): List<String> = clienteRepositorio.obtenerProductoCoca()
    suspend fun obtenerPrecioCoca(producto:String):Double = clienteRepositorio.obtenerPrecioCoca(producto)
    suspend fun obtenerPrecioPrix(producto:String):Double = clienteRepositorio.obtenerPrecioPrix(producto)
    suspend fun obtenerAmoroso(): List<String> = clienteRepositorio.obtenerAmoroso()
}