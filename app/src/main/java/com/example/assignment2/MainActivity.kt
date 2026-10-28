package com.example.assignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assignment2.data.model.MealViewModel
import com.example.assignment2.ui.theme.AppNavHost
import com.example.assignment2.ui.theme.Assignment2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            Assignment2Theme {
                val mealViewModel: MealViewModel = viewModel()
                AppNavHost(viewModel = mealViewModel)
            }
        }
    }
}


