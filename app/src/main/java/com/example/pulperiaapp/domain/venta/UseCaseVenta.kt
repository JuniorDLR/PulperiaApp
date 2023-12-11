package com.example.pulperiaapp.domain.venta

import com.example.pulperiaapp.data.Repository.VentaRepositorio

import com.example.pulperiaapp.data.database.entitie.VentaPrixCoca

import javax.inject.Inject

class UseCaseVenta @Inject constructor(private val repositorio: VentaRepositorio) {

    suspend fun insertarVenta(ventaPrixCoca: VentaPrixCoca) =
        repositorio.insertarVenta(ventaPrixCoca)

    suspend fun obtenerVentaIndividual(
        fechaInicio: String,
        fechaFin: String
    ): List<VentaPrixCocaDetalle> {
        return repositorio.obtenerVentaIndividual(fechaInicio, fechaFin)
    }

    suspend fun obtenerVentaCajilla(
        fechaInicio: String,
        fechaFin: String
    ): List<VentaPrixCocaDetalle> {
        return repositorio.obtenerVentaCajilla(fechaInicio, fechaFin)
    }



    suspend fun obtenerFilterIndividual(
        fechaInicio: String
    ): List<VentaPrixCocaDetalle> {
        return repositorio.obtenerFilterIndividual(fechaInicio)
    }

    suspend fun obtenerFilterCajilla(
        fechaInicio: String
    ): List<VentaPrixCocaDetalle> {
        return repositorio.obtenerFilterCajilla(fechaInicio)
    }

    suspend fun obtenerTotal(fechaInicio: String, fechaFin: String): Double {
        return repositorio.obtenerTotal(fechaInicio,fechaFin)
    }

    suspend fun editarVenta(ventaPrixCoca: VentaPrixCoca) =
        repositorio.editarVenta(ventaPrixCoca)

    suspend fun obtenerProductoPrix(): List<String> {
        return repositorio.obtenerProductoPrix()
    }

    suspend fun obtenerProdcutoCoca(): List<String> {
        return repositorio.obtenerProductoCoca()
    }

    suspend fun eliminarVenta(id: Int) = repositorio.eliminarVenta(id)
    suspend fun obtenerProductoBig(): List<String> {
        return repositorio.obtenerProdcutoBig()
    }

    suspend fun obtenerPrecioPrix(producto: String): Double {
        return repositorio.obtenerPrecioPrix(producto)

    }


    suspend fun obtenerPrecioCoca(producto: String): Double {
        return repositorio.obtenerPrecioCoca(producto)

    }

    suspend fun obtenerPrecioBig(producto: String): Double {
        return repositorio.obtenerPrecioBig(producto)
    }

    suspend fun obtenerDetalleEditar(idFecha: String): List<DetalleEditar> =
        repositorio.obtenerDetalleEditar(idFecha)


}