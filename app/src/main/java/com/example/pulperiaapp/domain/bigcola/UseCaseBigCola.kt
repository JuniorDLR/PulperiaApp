package com.example.pulperiaapp.domain.bigcola

import com.example.pulperiaapp.data.repository.TablaBigRepositorio
import com.example.pulperiaapp.data.database.entitie.PrecioBigCola
import javax.inject.Inject

class UseCaseBigCola @Inject constructor(val bigRepositorio: TablaBigRepositorio) {


    suspend fun insertarPrecioBig(precioBigCola: PrecioBigCola) =
        bigRepositorio.insertarPrecioBig(precioBigCola)

    suspend fun obtenerBigCola(): List<TablaBig> = bigRepositorio.obtenerBigCola()
    suspend fun editarBigCola(precio: Double, id: Int) = bigRepositorio.editarBigCola(precio, id)
    suspend fun eliminarBigColca(id: Int) = bigRepositorio.eliminarBigCola(id)
    suspend fun obtenerPrecioId(id: Int):Double = bigRepositorio.obtenerPrecioId(id)
}