package com.example.pulperiaapp.ui.view.bigcola.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulperiaapp.data.database.entitie.PrecioBigCola
import com.example.pulperiaapp.domain.bigcola.TablaBig
import com.example.pulperiaapp.domain.bigcola.UseCaseBigCola
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BigColaViewModel @Inject constructor(private val useBig: UseCaseBigCola) : ViewModel() {


    private val _bigModel = MutableLiveData<List<TablaBig>>()
    val bigModdel: LiveData<List<TablaBig>> = _bigModel


    fun insertarBigCola(precioBigCola: PrecioBigCola) {
        viewModelScope.launch {
            useBig.insertarPrecioBig(precioBigCola)
            actualizar()
        }
    }

    fun editarBigCola(precio: Double, id: Int) {
        viewModelScope.launch {
            useBig.editarBigCola(precio, id)
            actualizar()
        }
    }

    fun eliminarBigCola(id: Int) {
        viewModelScope.launch {
            useBig.eliminarBigColca(id)
            actualizar()
        }
    }


    suspend fun obtenerBigcola(): List<TablaBig> {

        return try {
            val lista = useBig.obtenerBigCola()
            actualizar()
            lista
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()

        }

    }

    private fun actualizar(list: List<TablaBig>? = null) {
        viewModelScope.launch {
            val esNo = list ?: useBig.obtenerBigCola()
            _bigModel.value = esNo
        }

    }

    suspend fun obtenerPrecioId(precioId: Int): Double {
        return useBig.obtenerPrecioId(precioId)
    }
}