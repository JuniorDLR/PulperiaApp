package com.example.pulperiaapp.ui.view.filtrado

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FiltrarDatosViewModel @Inject constructor(private val state: SavedStateHandle) : ViewModel() {


    var fecha: String?
        get() = state.get<String>("fecha")

        set(value) {
            state["fecha"] = value
        }

    var individualChecked: Boolean
        get() = state.get<Boolean>("individualChecked") ?: false
        set(value) {
            state["individualChecked"] = value
        }

    var cajillaChecked: Boolean
        get() = state.get<Boolean>("cajillaChecked") ?: false
        set(value) {
            state["cajillaChecked"] = value
        }

     private var selectedRdioButtonId: Int
        get() = state.get<Int>("selectedRdioButtonId") ?: -1
       private set(value) {
            state["selectedRdioButtonId"] = value
        }


    fun saveRadioButtonState(radioButton: Int) {
        selectedRdioButtonId = radioButton
    }

    fun isRadioButtonChecked(radioButtonId: Int): Boolean {
        return selectedRdioButtonId == radioButtonId
    }

    fun saveState(outState: Bundle) {
        outState.putString("fecha", fecha)
        outState.putBoolean("individualChecked", individualChecked)
        outState.putBoolean("cajillaChecked", cajillaChecked)
        outState.putInt("selectedRdioButtonId", selectedRdioButtonId)

    }


    fun restoreState(savedInstanceState: Bundle) {

        fecha = savedInstanceState.getString("fecha")
        individualChecked = savedInstanceState.getBoolean("individualChecked")
        cajillaChecked = savedInstanceState.getBoolean("cajillaChecked")
        selectedRdioButtonId = savedInstanceState.getInt("selectedRdioButtonId")
    }


}