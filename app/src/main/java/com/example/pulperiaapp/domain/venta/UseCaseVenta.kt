package com.example.pulperiaapp.domain.venta

import com.example.pulperiaapp.data.Repository.VentaRepositorio

import com.example.pulperiaapp.data.database.entitie.VentaPrixCoca

import javax.inject.Inject

class UseCaseVenta @Inject constructor(private val repositorio: VentaRepositorio) {

    suspend fun insertarVenta(ventaPrixCoca: VentaPrixCoca) =
        repositorio.insertarVenta(ventaPrixCoca)

    suspend fun obtenerVenta(): List<VentaPrixCocaDetalle> {
        return repositorio.obtenerVenta()
    }

    suspend fun obtenerTotal(): Double {
        return repositorio.obtenerTotal()
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

    suspend fun obtenerDetalleEditar(id: Int): List<DetalleEditar> =
        repositorio.obtenerDetalleEditar(id)

    fun pagin() = repositorio.paginSource()
}