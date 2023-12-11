package com.example.pulperiaapp.ui.view.prix.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulperiaapp.data.database.entitie.PrecioPrixEntity
import com.example.pulperiaapp.domain.prix.TablaPrix
import com.example.pulperiaapp.domain.prix.UseCasePrix
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PrixViewModel @Inject constructor(
    private val useCasePrix: UseCasePrix
) : ViewModel() {


    private val _prixModel = MutableLiveData<List<TablaPrix>>()
    val prixModel: LiveData<List<TablaPrix>> = _prixModel


    fun insertraPrixTabla(prix: PrecioPrixEntity) {
        viewModelScope.launch {
            useCasePrix.insertarPrixTabla(prix)
            val lista: List<TablaPrix> = useCasePrix.obtenerPrixTabla()
            _prixModel.postValue(lista)

        }
    }

    fun obtenerPrixTabla() {

        viewModelScope.launch {
            val lista = useCasePrix.obtenerPrixTabla()
            _prixModel.postValue(lista)
        }

    }


    fun editarPrixTabla(id: Int, precio: Double) {
        viewModelScope.launch {
            useCasePrix.editarPrixTabla(id, precio)
            val lista: List<TablaPrix> = useCasePrix.obtenerPrixTabla()
            _prixModel.postValue(lista)
        }
    }

    fun eliminarPrixTabla(id: Int) {
        viewModelScope.launch {
            useCasePrix.eliminarPrixTabla(id)
            val lista: List<TablaPrix> = useCasePrix.obtenerPrixTabla()
            _prixModel.postValue(lista)
        }
    }
}