package com.example.assignment2.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.assignment2.data.model.MealType

@Composable
fun MealTypeDropdown(selectedMealType: MealType, onMealTypeChange: (MealType) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) {
            Text(selectedMealType.displayName)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            MealType.entries.forEach { mealType ->
                DropdownMenuItem(
                    text = { Text(mealType.displayName) },
                    onClick = {
                        onMealTypeChange(mealType)
                        expanded = false
                    }
                )

            }
        }
    }
}