package com.example.assignment2.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CalorieSummary(totalCalories: Double, dailyGoal: Double) {
    Text(text = "Total Calories: $totalCalories / $dailyGoal kcal")
}