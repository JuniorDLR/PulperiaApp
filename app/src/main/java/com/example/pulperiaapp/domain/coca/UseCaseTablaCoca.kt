package com.example.pulperiaapp.domain.coca

import com.example.pulperiaapp.data.repository.TablaCocaRepository
import com.example.pulperiaapp.data.database.entitie.PrecioCocaEntity
import javax.inject.Inject

class UseCaseTablaCoca @Inject constructor(private val cocaRepository: TablaCocaRepository) {


    suspend fun insertarCocaTabla(precioCocaEntity: PrecioCocaEntity) {
        cocaRepository.insertarCocaTabla(precioCocaEntity)
    }

    suspend fun obtenerCocaTabla(): List<TablaCoca> {

        return cocaRepository.obtenerCocaTabla()
    }

    suspend fun editarCocaTabla(id: Int, precio: Double) =
        cocaRepository.editarCocaTabla(id, precio)

    suspend fun eliminarProducto(id: Int) = cocaRepository.eliminarProducto(id)
    suspend fun obtenerPrecioId(precioId: Int):Double = cocaRepository.obtenerPrecioId(precioId)


}