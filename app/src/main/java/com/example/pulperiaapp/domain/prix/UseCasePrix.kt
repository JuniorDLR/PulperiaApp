package com.example.pulperiaapp.domain.prix

import com.example.pulperiaapp.data.repository.TablaPrixRepsitory
import com.example.pulperiaapp.data.database.entitie.PrecioPrixEntity
import javax.inject.Inject

class UseCasePrix @Inject constructor(private val tablaPrixRepository: TablaPrixRepsitory) {

    suspend fun insertarPrixTabla(PrixEntity: PrecioPrixEntity) {
        tablaPrixRepository.insertarPrixTabla(PrixEntity)

    }

    suspend fun obtenerPrixTabla(): List<TablaPrix> {

        return tablaPrixRepository.obtenerPrixTabla()
    }

    suspend fun editarPrixTabla(id: Int, precio: Double) =
        tablaPrixRepository.editarPrixTabla(id, precio)

    suspend fun eliminarPrixTabla(id: Int) = tablaPrixRepository.eliminarPrixTabla(id)
    suspend fun obtenerPrecioId(precioId: Int):Double = tablaPrixRepository.obtenerPrecioId(precioId)


}