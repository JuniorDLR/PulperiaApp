package com.example.pulperiaapp.ui.view.venta.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulperiaapp.data.database.entitie.VentaPrixCoca
import com.example.pulperiaapp.domain.venta.DetalleEditar
import com.example.pulperiaapp.domain.venta.UseCaseVenta
import com.example.pulperiaapp.domain.venta.VentaPrixCocaDetalle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VentaViewModel @Inject constructor(private val useCaseVenta: UseCaseVenta) : ViewModel() {


    private val _ventaModelCajilla =
        MutableStateFlow<Map<String, List<VentaPrixCocaDetalle>>>(emptyMap())
    val ventaModelCajilla: StateFlow<Map<String, List<VentaPrixCocaDetalle>>> = _ventaModelCajilla

    private val _ventaModelIndividual = MutableStateFlow<List<VentaPrixCocaDetalle>>(emptyList())
    val ventaModelIndividual: StateFlow<List<VentaPrixCocaDetalle>> = _ventaModelIndividual

    private val _obtenerTotal = MutableStateFlow<Double?>(null)
    val obtenerTotal: StateFlow<Double?> = _obtenerTotal

    private val _obtenerGanacia = MutableStateFlow<Double?>(null)
    val obtenerGanacia: StateFlow<Double?> = _obtenerGanacia

    private val _data = MutableStateFlow<List<DetalleEditar>>(emptyList())
    val data: StateFlow<List<DetalleEditar>> = _data


    fun insertarVenta(ventaPrixCoca: VentaPrixCoca) {
        viewModelScope.launch {
            useCaseVenta.insertarVenta(ventaPrixCoca)

        }
    }


    suspend fun editarVenta(ventaPrixCoca: VentaPrixCoca) = useCaseVenta.editarVenta(ventaPrixCoca)

    suspend fun obtenerVentaIndividual(
        fechaInicio: String, fechaFin: String
    ): List<VentaPrixCocaDetalle> {

        return try {
            val lista =
                useCaseVenta.obtenerVentaIndividual(fechaInicio = fechaInicio, fechaFin = fechaFin)
            _ventaModelIndividual.value = lista
            lista
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }


    }

    suspend fun obtenerVentaCajilla(
        fechaInicio: String, fechaFin: String
    ): List<VentaPrixCocaDetalle> {

        return try {

            val lista =
                useCaseVenta.obtenerVentaCajilla(fechaInicio = fechaInicio, fechaFin = fechaFin)
            _ventaModelCajilla.value = lista.groupBy { it.fechaVenta }
            lista

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()

        }
    }


    suspend fun obtenerFilterIndividual(
        fechaInicio: String, fechaFin: String
    ): List<VentaPrixCocaDetalle> {

        return try {
            val lista =
                useCaseVenta.obtenerFilterIndividual(fechaInicio = fechaInicio, fechaFin = fechaFin)

            _ventaModelIndividual.value = lista
            lista
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ViewModel", "Error al obtener individual  $e")
            emptyList()
        }


    }

    suspend fun obtenerFilterCajilla(
        fechaInicio: String, fechaFin: String
    ): List<VentaPrixCocaDetalle> {

        return try {

            val lista =
                useCaseVenta.obtenerFilterCajilla(fechaInicio = fechaInicio, fechaFin = fechaFin)

            _ventaModelCajilla.value = lista.groupBy { it.fechaVenta }
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

    fun obtenerTotal(fechaInicio: String, fechaFin: String) {
        viewModelScope.launch {
            val total: Double? = useCaseVenta.obtenerTotal(fechaInicio, fechaFin)

            _obtenerTotal.value = total ?: 0.0
        }
    }

    fun obtenerGananciasEntreFechas(fechaInicio: String, fechaFin: String) {
        viewModelScope.launch {

            val ganacia: Double? = useCaseVenta.obtenerGananciasEntreFechas(fechaInicio, fechaFin)
            _obtenerGanacia.value = ganacia ?: 0.0

        }


    }


    suspend fun obtenerProductoBig(): List<String> {
        return useCaseVenta.obtenerProductoBig()
    }

    suspend fun obtenerTodosLosProductos(): List<String> {
        val listaBig = useCaseVenta.obtenerProductoBig()
        val listaCoca = useCaseVenta.obtenerProdcutoCoca()
        val listaPrix = useCaseVenta.obtenerProductoPrix()
        return listaBig + listaCoca + listaPrix
    }

    fun eliminarVenta(id: Int) {
        viewModelScope.launch {
            useCaseVenta.eliminarVenta(id)
        }
    }


    suspend fun obtenerPrecioPrix(producto: String): Double? {
        return useCaseVenta.obtenerPrecioPrix(producto)
    }


    suspend fun obtenerPrecioCoca(producto: String): Double? {
        return useCaseVenta.obtenerPrecioCoca(producto)
    }

    suspend fun obtenerPrecioBig(producto: String): Double? {
        return useCaseVenta.obtenerPrecioBig(producto)
    }


    suspend fun obtenerDetalleEditarLiveData(idFecha: String): List<DetalleEditar> {
        return try {
            val lista: List<DetalleEditar> = useCaseVenta.obtenerDetalleEditar(idFecha)
            _data.value = lista
            lista
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun obtenerPrecio(producto: String): Double {

        val precioPrix = obtenerPrecioPrix(producto)
        if (precioPrix != null) {
            return precioPrix
        }

        val precioCoca = obtenerPrecioCoca(producto)
        if (precioCoca != null) {
            return precioCoca
        }

        val precioBig = obtenerPrecioBig(producto)
        if (precioBig != null) {
            return precioBig
        }


        throw NoSuchElementException("El producto $producto no se encuentra en ninguna lista.")
    }


}