package com.example.assignment2.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.assignment2.data.model.ManualEntryEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


@Composable
fun ManualEntryDialog(
    event: ManualEntryEvent,
    onSubmit: (Double, Double, Double, Double) -> Unit,
    onCancel: () -> Unit
) {
    var calories by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
    var proteins by remember { mutableStateOf("") }
    var carbohydrates by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Manual Nutritional Entry") },
        text = {
            Column {
                Text("Enter nutritional information for ${event.foodName}")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Calories") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                TextField(
                    value = fats,
                    onValueChange = { fats = it },
                    label = { Text("Fats (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                TextField(
                    value = proteins,
                    onValueChange = { proteins = it },
                    label = { Text("Proteins (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                TextField(
                    value = carbohydrates,
                    onValueChange = { carbohydrates = it },
                    label = { Text("Carbohydrates (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val cal = calories.toDoubleOrNull() ?: 0.0
                val fat = fats.toDoubleOrNull() ?: 0.0
                val prot = proteins.toDoubleOrNull() ?: 0.0
                val carbs = carbohydrates.toDoubleOrNull() ?: 0.0
                onSubmit(cal, fat, prot, carbs)
            }) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}
