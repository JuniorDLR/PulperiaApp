package com.example.pulperiaapp.ui.view.credito.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.pulperiaapp.data.database.entitie.CreditoEntity

import com.example.pulperiaapp.domain.amoroso.UseCaseAmoroso
import com.example.pulperiaapp.domain.amoroso.VentaAmorosoDetalle

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class CreditoViewModel @Inject constructor(
    private val useCaseAmoroso: UseCaseAmoroso
) :
    ViewModel() {
    private val _groupedAmorosoModel = MutableLiveData<Map<String, List<VentaAmorosoDetalle>>>()
    val groupedAmorosoModel: LiveData<Map<String, List<VentaAmorosoDetalle>>> = _groupedAmorosoModel


    fun insertarCredito(creditoEntity: MutableList<CreditoEntity>) {
        viewModelScope.launch {
            useCaseAmoroso.insertarCredito(creditoEntity)
            actualizarDatos()
        }
    }


    fun eliminarCredito(id: Int) {
        viewModelScope.launch {
            useCaseAmoroso.eliminarCredito(id)
            actualizarDatos()
        }
    }

    fun actualizarEstado(nuevoEstado: Boolean, cliente: String) {
        viewModelScope.launch {
            useCaseAmoroso.actualizarpago(nuevoEstado, cliente)
            actualizarDatos()

        }
    }


    suspend fun editarCredito(
        id: Int,
        producto: String,
        cantidad: Int,
        precio: Double,
        fecha: String
    ) {
        useCaseAmoroso.editarCredito(id, producto, cantidad, precio, fecha)

    }

    suspend fun obtenerDetalleAmoroso(cliente: String) {
        try {
            val lista: List<VentaAmorosoDetalle> = useCaseAmoroso.obtenerDetalleAmoroso(cliente)
            val groupBy = lista.groupBy { it.cliente }
            _groupedAmorosoModel.postValue(groupBy)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    suspend fun obtenerFilterPago(fechaFilter: String): List<VentaAmorosoDetalle> {
        return try {
            val lista = useCaseAmoroso.obtenerFilterPago(fechaFilter)
            val groupClient = lista.groupBy { it.cliente }
            _groupedAmorosoModel.postValue(groupClient)

            lista
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun actualizarDatos(lista: List<VentaAmorosoDetalle>? = null) {
        viewModelScope.launch {
            val listaVentas = lista ?: useCaseAmoroso.obtenerCredito()
            val ventasFiltradas = listaVentas.filter { !it.estadoPago }
            _groupedAmorosoModel.value = ventasFiltradas.groupBy { it.cliente }
        }
    }


}


