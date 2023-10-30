package com.example.pulperiaapp.ui.view.credito.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pulperiaapp.data.database.entitie.ClienteEntity
import com.example.pulperiaapp.domain.amoroso.UseCaseCliente
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ClienteViewModel @Inject constructor(private val useCaseCliente: UseCaseCliente) :
    ViewModel() {


    suspend fun obtenerProductoPrix(): List<String> = useCaseCliente.obtenerProductoPrix()
    suspend fun obtenerProductoCoca(): List<String> = useCaseCliente.obtenerProductoCoca()
    suspend fun insertarCliente(nombre: ClienteEntity) = useCaseCliente.insertarCliente(nombre)

    suspend fun obtenerPrecioCoca(producto: String): Double {
        return useCaseCliente.obtenerPrecioCoca(producto)
    }


    suspend fun obtenerPrecioPrix(producto: String): Double {
        return useCaseCliente.obtenerPrecioPrix(producto)
    }

    suspend fun obtenerAmoros(): List<String> = useCaseCliente.obtenerAmoroso()

}