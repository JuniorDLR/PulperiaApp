package com.example.pulperiaapp.domain.usuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulperiaapp.data.database.entitie.UsuarioEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsuarioViewModel @Inject constructor(private val useCase: UsuarioUseCase) : ViewModel() {


    fun agregarUsuario(usuarioEntity: UsuarioEntity) {
        viewModelScope.launch {
            useCase.agregarUsuario(usuarioEntity)
        }
    }

    suspend fun obtenerUsuario(): List<UsuarioModel> {
        return try {
            val lista = useCase.obtenerUsuario()
            lista

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun editarUsuario(pw: String, idUsuario: Int) {
        viewModelScope.launch {
            useCase.editarUsuario(pw, idUsuario)
        }
    }
}