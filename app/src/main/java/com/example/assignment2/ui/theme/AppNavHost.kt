package com.example.assignment2.ui.theme


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assignment2.data.model.MealViewModel
import com.example.assignment2.ui.screens.*

@Composable
fun AppNavHost(viewModel: MealViewModel) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "main") {
        composable("main") {
            MainScreen(viewModel, onNavigateToSearch = {
                navController.navigate("search")
            })
        }
        composable("search") {
            FoodSearchScreen(viewModel)
        }
    }
}



