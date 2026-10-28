package com.example.assignment2.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.assignment2.data.network.FoodItem

@Composable
fun FoodItemCard(foodItem: FoodItem, portionSize: String, onAddMeal: (FoodItem) -> Unit) {
    val size = portionSize.toDoubleOrNull() ?: 100.0
    val factor = size / 100.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = foodItem.name)
            Text(text = "Calories: ${"%.2f".format(foodItem.calories * factor)} kcal")
            Text(text = "Fats: ${"%.2f".format(foodItem.fatTotal * factor)} g")
            Text(text = "Proteins: ${"%.2f".format(foodItem.protein * factor)} g")
            Text(text = "Carbs: ${"%.2f".format(foodItem.carbohydrates * factor)} g")
            Button(
                onClick = { onAddMeal(foodItem) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add to Log")
            }
        }
    }
}