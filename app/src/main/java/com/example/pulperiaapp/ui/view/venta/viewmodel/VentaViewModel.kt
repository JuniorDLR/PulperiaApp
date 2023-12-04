package com.example.pulperiaapp.ui.view.venta.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn

import com.example.pulperiaapp.data.database.entitie.VentaPrixCoca
import com.example.pulperiaapp.domain.venta.DetalleEditar

import com.example.pulperiaapp.domain.venta.UseCaseVenta
import com.example.pulperiaapp.domain.venta.VentaPrixCocaDetalle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ITEMS_PER_PAGE = 10

@HiltViewModel
class VentaViewModel @Inject constructor(private val useCaseVenta: UseCaseVenta) : ViewModel() {


    private val _ventaModelCajilla =
        MutableStateFlow<Map<String, List<VentaPrixCocaDetalle>>>(emptyMap())
    val ventaModelCajilla: StateFlow<Map<String, List<VentaPrixCocaDetalle>>> = _ventaModelCajilla

    private val _ventaModelIndividual =
        MutableStateFlow<List<VentaPrixCocaDetalle>>(emptyList())
    val ventaModelIndividual: StateFlow<List<VentaPrixCocaDetalle>> =
        _ventaModelIndividual


    val _obtenerTotal = MutableLiveData<Double>()
    val obtenerTotal: LiveData<Double> = _obtenerTotal

    private val _data = MutableLiveData<List<DetalleEditar>>()
    val data: LiveData<List<DetalleEditar>> = _data


    fun insertarVenta(ventaPrixCoca: VentaPrixCoca) {
        viewModelScope.launch {
            useCaseVenta.insertarVenta(ventaPrixCoca)

        }
    }

    fun obtenerTotal() {
        viewModelScope.launch {
            val total: Double? = useCaseVenta.obtenerTotal()

            _obtenerTotal.value = total ?: 0.0
        }
    }

    suspend fun editarVenta(ventaPrixCoca: VentaPrixCoca) = useCaseVenta.editarVenta(ventaPrixCoca)

    suspend fun obtenerVentaIndividual(fechaInicio: String, fechaFin: String): List<VentaPrixCocaDetalle> {

        return try {
            val lista = useCaseVenta.obtenerVentaIndividual(fechaInicio = fechaInicio, fechaFin = fechaFin)
            _ventaModelIndividual.value = lista
            lista
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }


    }

    suspend fun obtenerVentaCajilla(fechaInicio: String, fechaFin: String): List<VentaPrixCocaDetalle> {

        return try {
            val lista = useCaseVenta.obtenerVentaCajilla(fechaInicio = fechaInicio, fechaFin = fechaFin)
            val listaGroup = lista.groupBy { it.fecha_venta }
            _ventaModelCajilla.value = listaGroup
            lista

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()

        }
    }


    suspend fun obtenerProdcutoPrix(): List<String> {
        return useCaseVenta.obtenerProductoPrix()
    }

    suspend fun obtenerProductoCoca(): List<String> {
        return useCaseVenta.obtenerProdcutoCoca()
    }

    suspend fun eliminarVenta(id: Int) {
        useCaseVenta.eliminarVenta(id)
        obtenerTotal()
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

    suspend fun obtenerDetalleEditarLiveData(idInventario: String): List<DetalleEditar> {
        return try {
            val lista: List<DetalleEditar> = useCaseVenta.obtenerDetalleEditar(idInventario)
            _data.postValue(lista)
            lista
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun obtenerPrecioBig(producto: String): Double {
        return useCaseVenta.obtenerPrecioBig(producto)
    }


}