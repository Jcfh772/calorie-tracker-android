package com.example.assignment2.data.network

import com.google.gson.annotations.SerializedName


data class NutritionResponse(
    val items: List<FoodItem>
)

data class FoodItem(
    @SerializedName("name") val name: String,
    @SerializedName("calories") val calories: Double,
    @SerializedName("fat_total_g") val fatTotal: Double,
    @SerializedName("protein_g") val protein: Double,
    @SerializedName("carbohydrates_total_g") val carbohydrates: Double
)
