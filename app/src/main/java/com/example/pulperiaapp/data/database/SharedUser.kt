package com.example.pulperiaapp.data.database

import android.content.Context
import java.util.Calendar
import javax.inject.Inject

class SharedUser @Inject constructor(context: Context) {
    private val NOMBRE_SHARED = "NOMBRE"
    private val CODIGO_SAHRED = "CODIGO"
    private val ULTIMO_ENVIO_SHARED = "ULTIMO_ENVIO"

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

    fun guardarUltimoEnvio(){
        editor.putLong(ULTIMO_ENVIO_SHARED,Calendar.getInstance().timeInMillis)
        editor.apply()
    }

    fun isOneDayPassed():Boolean{
        val lastSentTime = shared.getLong(ULTIMO_ENVIO_SHARED,0)
        val currentTime = Calendar.getInstance().timeInMillis
        return currentTime - lastSentTime>= ONE_DAY_IN_MILLIS
    }
    companion object {
        private const val ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000 // 1 día en milisegundos
    }
}