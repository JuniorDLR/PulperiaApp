package com.example.pulperiaapp.domain.coca

import com.example.pulperiaapp.data.Repository.TablaCocaRepository
import com.example.pulperiaapp.data.database.entitie.coca.PrecioCocaEntity
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


}