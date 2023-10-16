package com.example.pulperiaapp.ui.view.credito.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulperiaapp.data.database.entitie.credito.CreditoEntity
import com.example.pulperiaapp.domain.amoroso.UseCaseAmoroso
import com.example.pulperiaapp.domain.amoroso.VentaAmorosoDetalle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreditoViewModel @Inject constructor(private val useCaseAmoroso: UseCaseAmoroso) :
    ViewModel() {


    private val _amorosoModel = MutableLiveData<List<VentaAmorosoDetalle>>()
    val amorosoModel: LiveData<List<VentaAmorosoDetalle>> = _amorosoModel


    fun insertarCredito(creditoEntity: CreditoEntity) {

        viewModelScope.launch {
            useCaseAmoroso.insertarCredito(creditoEntity)
            val lista = useCaseAmoroso.obtenerCredito()
            actualizarDatos(lista)

        }
    }

    fun actualizarDatos(lista: List<VentaAmorosoDetalle>) {
        _amorosoModel.value = lista

    }

    fun obtenerCredito() {
        viewModelScope.launch {
            val lista = useCaseAmoroso.obtenerCredito()
            _amorosoModel.postValue(lista)
        }
    }
}