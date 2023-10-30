package com.example.pulperiaapp.data.Repository

import com.example.pulperiaapp.data.database.dao.VentaCocaPrix
import com.example.pulperiaapp.data.database.entitie.VentaPrixCoca

import com.example.pulperiaapp.ui.view.venta.viewmodel.VentaPrixCocaDetalle
import javax.inject.Inject

class VentaRepositorio @Inject constructor(private val ventaCocaPrix: VentaCocaPrix) {


    suspend fun insertarVenta(ventaPrixCoca: VentaPrixCoca) =
        ventaCocaPrix.insertatVenta(ventaPrixCoca)

    suspend fun obtenerVenta(): List<VentaPrixCocaDetalle> {

        return ventaCocaPrix.obtenerVenta()
    }


    suspend fun obtenerProductoPrix(): List<String> {

        return ventaCocaPrix.obtenerProductoPrix()
    }

    suspend fun obtenerProductoCoca(): List<String> {
        return ventaCocaPrix.obtenerProdcutoCoca()

    }

    suspend fun obtenerPrecioPrix(producto: String): Double {
        return ventaCocaPrix.obtenerPrecioPrix(producto)
    }

    suspend fun obtenerPrecioCoca(producto: String): Double {
        return ventaCocaPrix.obtenerPrecioCoca(producto)
    }
}