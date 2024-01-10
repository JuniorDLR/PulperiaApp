package com.example.pulperiaapp.data.repository

import com.example.pulperiaapp.data.database.dao.UsuarioDao
import com.example.pulperiaapp.data.database.entitie.UsuarioEntity
import com.example.pulperiaapp.domain.usuario.UsuarioModel
import com.example.pulperiaapp.domain.usuario.toDomain
import javax.inject.Inject

class UsuarioRepositorio @Inject constructor(private val usuarioDao: UsuarioDao) {

    suspend fun agregarUsuario(usuarioEntity: UsuarioEntity) = usuarioDao.agregarUsuario(usuarioEntity)
    suspend fun obtenerUsuario(): List<UsuarioModel> {
        val response: List<UsuarioEntity> = usuarioDao.obtenerUsuario()
        return response.map { it.toDomain() }

    }

    suspend fun editarUsuario(pw:String,idUsuario:Int) = usuarioDao.editarUsuario(pw,idUsuario)
}