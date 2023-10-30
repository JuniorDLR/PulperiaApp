package com.example.pulperiaapp.ui.view.credito.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulperiaapp.data.database.entitie.CreditoEntity
import com.example.pulperiaapp.domain.amoroso.UseCaseAmoroso
import com.example.pulperiaapp.domain.amoroso.VentaAmorosoDetalle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreditoViewModel @Inject constructor(private val useCaseAmoroso: UseCaseAmoroso) :
    ViewModel() {

    private val _groupedAmorosoModel = MutableLiveData<Map<String, List<VentaAmorosoDetalle>>>()
    val groupedAmorosoModel: LiveData<Map<String, List<VentaAmorosoDetalle>>> = _groupedAmorosoModel


    fun insertarCredito(creditoEntity: MutableList<CreditoEntity>) {
        viewModelScope.launch {
            useCaseAmoroso.insertarCredito(creditoEntity)
            actualizarDatos()
        }
    }

    fun eliminarCredito(cliente: String) {
        viewModelScope.launch {
            useCaseAmoroso.eliminarCredito(cliente)
            actualizarDatos()
        }
    }

    fun actualizarEstado(nuevoEstado: Boolean, cliente: String) {
        viewModelScope.launch {
            useCaseAmoroso.actualizarpago(nuevoEstado, cliente)
            actualizarDatos()

        }
    }

    fun actualizarDatos(lista: List<VentaAmorosoDetalle>? = null) {
        viewModelScope.launch {
            val listaVentas = lista ?: useCaseAmoroso.obtenerCredito()
            val ventasFiltradas = listaVentas.filter { !it.estado_pago }
            _groupedAmorosoModel.value = ventasFiltradas.groupBy { it.cliente }
        }
    }



}


