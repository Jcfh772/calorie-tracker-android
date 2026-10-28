package com.example.assignment2.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assignment2.data.network.FoodItem

@Composable
fun FoodSearchResults(
    results: List<FoodItem>,
    portionSize: String,
    onAddMeal: (FoodItem) -> Unit
) {
    Column {

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(results) { foodItem ->
                FoodItemCard(foodItem, portionSize, onAddMeal)
            }
        }
    }
}

@Preview
@Composable
fun FoodSearchResultsPreview() {
    val sampleResults = listOf(
        FoodItem("Sample Food 1", 100.0, 5.0, 10.0, 15.0),
        FoodItem("Sample Food 2", 123.0, 8.0, 1.0, 152.0)
    )
    FoodSearchResults(sampleResults, "100", onAddMeal = {})
}