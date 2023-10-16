package com.example.pulperiaapp.domain.venta

import com.example.pulperiaapp.data.Repository.VentaRepositorio

import com.example.pulperiaapp.data.database.entitie.venta.VentaPrixCoca
import com.example.pulperiaapp.ui.view.venta.viewmodel.VentaPrixCocaDetalle

import javax.inject.Inject

class UseCaseVenta @Inject constructor(private val repositorio: VentaRepositorio) {

    suspend fun insertarVenta(ventaPrixCoca: VentaPrixCoca) =
        repositorio.insertarVenta(ventaPrixCoca)

    suspend fun obtenerVenta(): List<VentaPrixCocaDetalle> {
        return repositorio.obtenerVenta()
    }


    suspend fun obtenerProductoPrix(): List<String> {
        return repositorio.obtenerProductoPrix()
    }

    suspend fun obtenerProdcutoCoca(): List<String> {
        return repositorio.obtenerProductoCoca()
    }

    suspend fun obtenerPrecioPrix(producto: String): Double {
        return repositorio.obtenerPrecioPrix(producto)

    }

    suspend fun obtenerPrecioCoca(producto: String): Double {
        return repositorio.obtenerPrecioCoca(producto)

    }
}