package com.example.assignment2.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.assignment2.data.model.MealViewModel

@Composable
fun MealList(viewModel: MealViewModel) {
    val meals by viewModel.getMealDao().getAllMeals().collectAsState(initial = emptyList())
    LazyColumn {
        items(meals) { meal ->
            MealItem(meal, viewModel)
        }
    }
}