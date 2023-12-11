package com.example.pulperiaapp.data.Repository

import com.example.pulperiaapp.data.database.dao.VentaCocaPrix
import com.example.pulperiaapp.data.database.entitie.VentaPrixCoca
import com.example.pulperiaapp.domain.venta.DetalleEditar

import com.example.pulperiaapp.domain.venta.VentaPrixCocaDetalle
import com.example.pulperiaapp.domain.venta.toDomain
import javax.inject.Inject

class VentaRepositorio @Inject constructor(private val ventaCocaPrix: VentaCocaPrix) {


    suspend fun insertarVenta(ventaPrixCoca: VentaPrixCoca) =
        ventaCocaPrix.insertatVenta(ventaPrixCoca)

    suspend fun obtenerVentaIndividual(
        fechaInicio: String,
        fechaFin: String
    ): List<VentaPrixCocaDetalle> {
        val response: List<VentaPrixCoca> =
            ventaCocaPrix.obtenerVentaIndividual(fechaInicio, fechaFin)
        return response.map { it.toDomain() }
    }

    suspend fun obtenerVentaCajilla(
        fechaInicio: String,
        fechaFin: String
    ): List<VentaPrixCocaDetalle> {
        val response: List<VentaPrixCoca> = ventaCocaPrix.obtenerVentaCajilla(fechaInicio, fechaFin)
        return response.map { it.toDomain() }
    }

    suspend fun obtenerFilterIndividual(fechaInicio: String): List<VentaPrixCocaDetalle> {
        val response: List<VentaPrixCoca> = ventaCocaPrix.obtenerFilterIndividual(fechaInicio)
        return response.map { it.toDomain() }
    }

    suspend fun obtenerFilterCajilla(fechaInicio: String): List<VentaPrixCocaDetalle> {
        val response: List<VentaPrixCoca> = ventaCocaPrix.obtenerFilterCajilla(fechaInicio)
        return response.map { it.toDomain() }
    }

    suspend fun obtenerTotal(fechaInicio: String, fechaFin: String): Double {
        return ventaCocaPrix.obtenerTotal(fechaInicio, fechaFin)
    }

    suspend fun editarVenta(ventaPrixCoca: VentaPrixCoca) =
        ventaCocaPrix.editarVenta(ventaPrixCoca)

    suspend fun obtenerProductoPrix(): List<String> {

        return ventaCocaPrix.obtenerProductoPrix()
    }

    suspend fun obtenerProductoCoca(): List<String> {
        return ventaCocaPrix.obtenerProdcutoCoca()

    }

    suspend fun eliminarVenta(id: Int) = ventaCocaPrix.eliminarVenta(id)

    suspend fun obtenerProdcutoBig(): List<String> {
        return ventaCocaPrix.obtenerProdcutoBig()
    }

    suspend fun obtenerPrecioPrix(producto: String): Double {
        return ventaCocaPrix.obtenerPrecioPrix(producto)
    }


    suspend fun obtenerPrecioCoca(producto: String): Double {
        return ventaCocaPrix.obtenerPrecioCoca(producto)
    }

    suspend fun obtenerPrecioBig(producto: String): Double {
        return ventaCocaPrix.obtenerPrecioBig(producto)
    }

    suspend fun obtenerDetalleEditar(idFecha: String): List<DetalleEditar> =
        ventaCocaPrix.obtenerDetalleEditar(idFecha)

}

