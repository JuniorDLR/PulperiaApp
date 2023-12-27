package com.example.pulperiaapp.ui.view.principal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.example.pulperiaapp.R
import com.example.pulperiaapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView = binding.NavigationBottom
        navController = Navigation.findNavController(this, R.id.fragmentContainerView)
        navView.setupWithNavController(navController)

        navView.isVisible = false



        savedInstanceState?.let { bundle ->
            val isNavViewVisible = bundle.getBoolean("isNavViewVisible",true)
            binding.NavigationBottom.isVisible = isNavViewVisible
        }


    }


    override fun onSaveInstanceState(outState: Bundle) {
        // Guardar el estado de la barra de navegación
        outState.putBoolean("isNavViewVisible", binding.NavigationBottom.isVisible)

        // Llamar al método de la superclase
        super.onSaveInstanceState(outState)
    }



}