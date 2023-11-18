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


    private val _ventaModel = MutableStateFlow<List<VentaPrixCocaDetalle>>(emptyList())
    val ventaModel: StateFlow<List<VentaPrixCocaDetalle>> = _ventaModel

    val _obtenerTotal = MutableLiveData<Double>()
    val obtenerTotal: LiveData<Double> = _obtenerTotal

    private val _data = MutableLiveData<List<DetalleEditar>>()
    val data: LiveData<List<DetalleEditar>> = _data


    private val _ventaModelPagin =
        MutableStateFlow<PagingData<VentaPrixCocaDetalle>>(PagingData.empty())
    val ventaModelPagin: StateFlow<PagingData<VentaPrixCocaDetalle>> = _ventaModelPagin


    init {
        viewModelScope.launch {
            Pager(
                config = PagingConfig(ITEMS_PER_PAGE, enablePlaceholders = false),
                pagingSourceFactory = { useCaseVenta.pagin() }
            ).flow
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _ventaModelPagin.value = pagingData
                }
        }
    }


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

    fun obtenerVenta() {
        viewModelScope.launch {
            val lista = useCaseVenta.obtenerVenta()
            actualizarDatos(lista)

        }
    }


    private fun actualizarDatos(lista: List<VentaPrixCocaDetalle>? = null) {
        viewModelScope.launch {
            val listaObtenida = lista ?: useCaseVenta.obtenerVenta()
            _ventaModel.value = listaObtenida
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
        obtenerVenta()
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

    suspend fun obtenerDetalleEditarLiveData(id: Int) {
        try {
            val lista: List<DetalleEditar> = useCaseVenta.obtenerDetalleEditar(id)
            _data.postValue(lista)
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }

    suspend fun obtenerPrecioBig(producto: String): Double {
        return useCaseVenta.obtenerPrecioBig(producto)
    }
}