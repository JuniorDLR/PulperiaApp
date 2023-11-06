package com.example.pulperiaapp.ui.view.inventario.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulperiaapp.data.database.entitie.InventarioEntity
import com.example.pulperiaapp.domain.inventario.InventarioModel
import com.example.pulperiaapp.domain.inventario.UseCaseInventario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventarioViewModel @Inject constructor(private val useCaseInventario: UseCaseInventario) :
    ViewModel() {


    private val _inventarioModel = MutableLiveData<Map<String, List<InventarioModel>>>()
    val inventarioModel: LiveData<Map<String, List<InventarioModel>>> = _inventarioModel


    fun insertarInventario(inventarioEntity: MutableList<InventarioEntity>) {
        viewModelScope.launch {
            useCaseInventario.insertarInventario(inventarioEntity)
            actualizarDatos()

        }
    }

    fun obtenerInventario() {
        viewModelScope.launch {
            useCaseInventario.obtenerInventario()
            actualizarDatos()
        }
    }

    fun actualizarDatos(lista: List<InventarioModel>? = null) {
        viewModelScope.launch {
            val response = lista ?: useCaseInventario.obtenerInventario()
            _inventarioModel.value = response.groupBy { it.fecha_entrega }
        }
    }

    fun eliminarInventario(id: Int) {
        viewModelScope.launch {
            useCaseInventario.eliminarInventario(id)
            actualizarDatos()
        }
    }


}