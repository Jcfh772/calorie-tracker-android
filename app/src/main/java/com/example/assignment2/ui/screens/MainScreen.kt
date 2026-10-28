package com.example.assignment2.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.assignment2.data.model.MealViewModel
import com.example.assignment2.ui.components.*

@Composable
fun MainScreen(viewModel: MealViewModel, onNavigateToSearch: () -> Unit) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 64.dp, top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(onClick = onNavigateToSearch) {

            Text("Search Food")
        }
        Spacer(modifier = Modifier.height(16.dp))
        CalorieSummary(viewModel.totalCalories, viewModel.dailyGoal)
        Spacer(modifier = Modifier.height(16.dp))
        MealList(viewModel)

    }
}