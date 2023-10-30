package com.example.pulperiaapp.domain.prix

import com.example.pulperiaapp.data.Repository.TablaPrixRepsitory
import com.example.pulperiaapp.data.database.entitie.PrecioPrixEntity
import javax.inject.Inject

class UseCasePrix @Inject constructor(private val tablaPrixRepsitory: TablaPrixRepsitory) {

    suspend fun insertarPrixTabla(PrixEntity: PrecioPrixEntity) {
        tablaPrixRepsitory.insertarPrixTabla(PrixEntity)

    }

    suspend fun obtenerPrixTabla(): List<TablaPrix> {

        return tablaPrixRepsitory.obtenerPrixTabla()
    }

    suspend fun editarPrixTabla(id: Int, precio: Double) =
        tablaPrixRepsitory.editarPrixTabla(id, precio)

    suspend fun eliminarPrixTabla(id: Int) = tablaPrixRepsitory.eliminarPrixTabla(id)
}