package com.example.pulperiaapp.ui.view.venta.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.pulperiaapp.data.database.entitie.VentaPrixCoca

import com.example.pulperiaapp.domain.venta.UseCaseVenta
import com.example.pulperiaapp.domain.venta.VentaPrixCocaDetalle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VentaViewModel @Inject constructor(private val useCaseVenta: UseCaseVenta) : ViewModel() {


    private val _ventaModel = MutableLiveData<List<VentaPrixCocaDetalle>>()
    val ventaModeL: LiveData<List<VentaPrixCocaDetalle>> = _ventaModel


    fun insertarVenta(ventaPrixCoca: VentaPrixCoca) {
        viewModelScope.launch {
            useCaseVenta.insertarVenta(ventaPrixCoca)
            val lista = useCaseVenta.obtenerVenta()
            actualizarVentas(lista)
        }
    }

    suspend fun obtenerTotal(): Double {
        return useCaseVenta.obtenerTotal()
    }

    fun actualizarVentas(nuevosDatos: List<VentaPrixCocaDetalle>) {
        _ventaModel.value = nuevosDatos
    }

    fun obtenerVenta() {
        viewModelScope.launch {
            val lista = useCaseVenta.obtenerVenta()
            _ventaModel.postValue(lista)
        }
    }

    suspend fun obtenerProdcutoPrix(): List<String> {
        return useCaseVenta.obtenerProductoPrix()
    }

    suspend fun obtenerProductoCoca(): List<String> {
        return useCaseVenta.obtenerProdcutoCoca()
    }

    suspend fun obtenerProductoBig(): List<String> {
        return useCaseVenta.obtenerProductoBig()
    }

    suspend fun obtenerPrecioPrix(producto: String): Double {
        return useCaseVenta.obtenerPrecioPrix(producto)
    }

    suspend fun obtenerPrecioCoca(producto: String): Double {
        return useCaseVenta.obtenerPrecioCoca(producto)
    }

    suspend fun obtenerPrecioBig(producto: String): Double {
        return useCaseVenta.obtenerPrecioBig(producto)
    }
}