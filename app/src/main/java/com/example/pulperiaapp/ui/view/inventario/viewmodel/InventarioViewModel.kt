package com.example.pulperiaapp.ui.view.inventario.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulperiaapp.data.database.entitie.InventarioEntity
import com.example.pulperiaapp.data.database.entitie.InventarioFotoEntity
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

    private val _groupInventario = MutableLiveData<List<InventarioModel>>()
    val groupInventario: LiveData<List<InventarioModel>> = _groupInventario

    fun insertarInventario(inventarioEntity: InventarioEntity) {
        viewModelScope.launch {
            useCaseInventario.insertarInventario(inventarioEntity)

            actualizarDatos()

        }
    }

    suspend fun obtenerFoto(): List<InventarioFotoEntity> = useCaseInventario.obtenerFoto()
    fun insertarFoto(fotos: List<InventarioFotoEntity>) {
        viewModelScope.launch {
            useCaseInventario.insertarFotos(fotos)
            actualizarDatos()
        }
    }

    suspend fun editarInventario(inventarioEntity: InventarioEntity) {

        useCaseInventario.editarInventario(inventarioEntity)
        actualizarDatos()


    }

    suspend fun obtenerInventario(): List<InventarioModel> {

        return try {
            val lista = useCaseInventario.obtenerInventario()
            actualizarDatos()
            lista
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()

        }

    }

    private fun actualizarDatos(lista: List<InventarioModel>? = null) {
        viewModelScope.launch {
            val response = lista ?: useCaseInventario.obtenerInventario()
            _inventarioModel.value = response.groupBy { it.fechaEntrega }
        }
    }

    suspend fun obtenerDetalleInventario(
        idInventario: String,
    ): List<InventarioModel> {
        return try {
            val lista = useCaseInventario.obtenerInventarioConFotos(idInventario)
            _groupInventario.postValue(lista)
            lista
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }


    }

    fun eliminarInventario(id: Int) {
        viewModelScope.launch {
            useCaseInventario.eliminarInventario(id)
            actualizarDatos()
        }
    }

    fun eliminarFoto(idFotos: String) {
        viewModelScope.launch {
            useCaseInventario.eliminarFoto(idFotos)
            actualizarDatos()
        }
    }


}