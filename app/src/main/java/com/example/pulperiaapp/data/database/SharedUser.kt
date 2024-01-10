package com.example.pulperiaapp.data.database

import android.content.Context
import javax.inject.Inject

class SharedUser @Inject constructor(context: Context) {
    private val NOMBRE_SHARED = "NOMBRE"
    private val CODIGO_SAHRED = "CODIGO"

    val shared = context.getSharedPreferences(NOMBRE_SHARED, 0)
    val editor = shared.edit()

    fun guardarCodigo(codigo: Int) {
        editor.putInt(CODIGO_SAHRED, codigo)
        editor.apply()
    }

    fun eliminarCodigo() {
        editor.clear()
        editor.apply()
    }

    fun mostrarCodigo(): Int {
        return shared.getInt(CODIGO_SAHRED, 0)
    }

}