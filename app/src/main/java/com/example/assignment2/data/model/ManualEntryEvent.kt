package com.example.assignment2.data.model


data class ManualEntryEvent(
    val foodName: String,
    val portionSize: Double,
    val mealType: MealType,
    val photoUrl: String? = null
)

