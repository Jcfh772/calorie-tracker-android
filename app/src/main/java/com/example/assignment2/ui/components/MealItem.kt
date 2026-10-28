package com.example.assignment2.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.assignment2.data.database.MealEntity
import com.example.assignment2.data.model.MealViewModel

@Composable
fun MealItem(mealEntity: MealEntity, viewModel: MealViewModel) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var editedFoodName by remember { mutableStateOf(mealEntity.foodName) }
    var editedCalories by remember { mutableStateOf(mealEntity.calories.toString()) }
    var editedFats by remember { mutableStateOf(mealEntity.fats.toString()) }
    var editedProteins by remember { mutableStateOf(mealEntity.proteins.toString()) }
    var editedCarbs by remember { mutableStateOf(mealEntity.carbohydrates.toString()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        // Load image from Firebase if photoUrl is available
        LaunchedEffect(mealEntity.photoUrl) {
            mealEntity.photoUrl?.let { url ->
                viewModel.downloadImageFromFirebase(
                    imageUrl = url,
                    onImageDownloaded = { downloadedBitmap ->
                        bitmap = downloadedBitmap // Set the downloaded bitmap
                    },
                    onError = {
                        // Handle the error
                        bitmap = null
                    }
                )
            }
        }


        // Display the Bitmap if it's loaded, otherwise show a placeholder
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "Meal Image",
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 8.dp)
            )
        } else {
            Text(text = "No Image", modifier = Modifier.size(64.dp))
        }

        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                if (isEditing) {
                    TextField(
                        value = editedFoodName,
                        onValueChange = { editedFoodName = it },
                        label = { Text("Food") }
                    )
                    TextField(
                        value = editedCalories,
                        onValueChange = { editedCalories = it },
                        label = { Text("kcal") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                    TextField(
                        value = editedFats,
                        onValueChange = { editedFats = it },
                        label = { Text("Fats") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                    TextField(
                        value = editedProteins,
                        onValueChange = { editedProteins = it },
                        label = { Text("Proteins") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                    TextField(
                        value = editedCarbs,
                        onValueChange = { editedCarbs = it },
                        label = { Text("Carbs") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                } else {
                    // Display meal details as Text when not editing
                    Text(text = "Type: ${mealEntity.mealType}")
                    Text(text = "Food: ${mealEntity.foodName}")
                    Text(text = "kcal: ${mealEntity.calories}")
                    Text(text = "Fats: ${mealEntity.fats}")
                    Text(text = "Proteins: ${mealEntity.proteins}")
                    Text(text = "Carb: ${mealEntity.carbohydrates}")
                }
            }

            Column (modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                if (isEditing) {
                    Button(onClick = {
                        // Save the updated values
                        val updatedMealEntity = mealEntity.copy(
                            foodName = editedFoodName,
                            calories = editedCalories.toDoubleOrNull() ?: mealEntity.calories,
                            fats = editedFats.toDoubleOrNull() ?: mealEntity.fats,
                            proteins = editedProteins.toDoubleOrNull() ?: mealEntity.proteins,
                            carbohydrates = editedCarbs.toDoubleOrNull() ?: mealEntity.carbohydrates
                        )
                        viewModel.updateMeal(updatedMealEntity)
                        isEditing = false
                    }) {
                        Text(text = "Save")
                    }
                    Button(onClick = { isEditing = false }) {
                        Text(text = "Cancel")
                    }
                } else {
                    Button(onClick = { isEditing = true }) {
                        Text(text = "Edit")
                    }
                    Button(onClick = {
                        viewModel.deleteMeal(mealEntity)
                    }) {
                        Text(text = "Delete")
                    }
                }
            }

        }
    }
}
