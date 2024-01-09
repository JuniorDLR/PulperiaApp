package com.example.pulperiaapp.domain.usuario

import com.example.pulperiaapp.data.database.entitie.UsuarioEntity

data class UsuarioModel(val usuario:String, val pw:String)

fun UsuarioEntity.toDomain() = UsuarioModel(usuario = usuario, pw = pw)