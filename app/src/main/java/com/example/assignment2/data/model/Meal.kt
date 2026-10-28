package com.example.assignment2.data.model

import java.time.LocalDate


enum class MealType(val displayName: String) {
    Breakfast("Breakfast"),
    Lunch("Lunch"),
    Dinner("Dinner"),
    Snack("Snack")
}


data class Meal(
    val id: Int = 0,
    val foodName: String,
    val portionSize: Double, // in grams
    val mealType: MealType,
    val calories: Double,
    val fats: Double,
    val proteins: Double,
    val carbohydrates: Double,
    val date: LocalDate = LocalDate.now(),
    val photoUrl: String? = null
)



