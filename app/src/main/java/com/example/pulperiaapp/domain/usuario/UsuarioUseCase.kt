package com.example.pulperiaapp.domain.usuario

import com.example.pulperiaapp.data.database.entitie.UsuarioEntity
import com.example.pulperiaapp.data.repository.UsuarioRepositorio
import javax.inject.Inject

class UsuarioUseCase @Inject constructor(private val usuarioRepositorio: UsuarioRepositorio) {

    suspend fun agregarUsuario(usuarioEntity: UsuarioEntity) =
        usuarioRepositorio.agregarUsuario(usuarioEntity)

   suspend fun obtenerUsuario():List<UsuarioModel> = usuarioRepositorio.obtenerUsuario()

}