package com.example.pulperiaapp.ui.view.coca.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulperiaapp.data.database.entitie.PrecioCocaEntity
import com.example.pulperiaapp.domain.coca.TablaCoca
import com.example.pulperiaapp.domain.coca.UseCaseTablaCoca
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CocaViewModel @Inject constructor(private val useCaseTablaCoca: UseCaseTablaCoca) :
    ViewModel() {


    private val _cocaViewModel = MutableLiveData<List<TablaCoca>>()
    val cocaViewModel: LiveData<List<TablaCoca>> = _cocaViewModel


    fun insertarCocaTabla(precioCocaEntity: PrecioCocaEntity) {

        viewModelScope.launch {
            useCaseTablaCoca.insertarCocaTabla(precioCocaEntity)
            val lista: List<TablaCoca> = useCaseTablaCoca.obtenerCocaTabla()
            _cocaViewModel.postValue(lista)

        }

    }

    suspend fun obtenerCocaTabla(): List<TablaCoca> {

        return try {
            val lista = useCaseTablaCoca.obtenerCocaTabla()
            _cocaViewModel.postValue(lista)
            lista
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }

    }

    fun editarCocaTabla(id: Int, precio: Double) {

        viewModelScope.launch {
            useCaseTablaCoca.editarCocaTabla(id, precio)
            val lista: List<TablaCoca> = useCaseTablaCoca.obtenerCocaTabla()
            _cocaViewModel.postValue(lista)
        }

    }

    fun eliminarProducto(id: Int) {
        viewModelScope.launch {
            useCaseTablaCoca.eliminarProducto(id)
            val lista: List<TablaCoca> = useCaseTablaCoca.obtenerCocaTabla()
            _cocaViewModel.postValue(lista)
        }
    }

    suspend fun obtenerPrecioId(precioId: Int): Double {
        return useCaseTablaCoca.obtenerPrecioId(precioId)
    }


}