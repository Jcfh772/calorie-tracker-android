package com.example.assignment2.data.model

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment2.data.database.AppDatabase
import com.example.assignment2.data.database.MealDao
import com.example.assignment2.data.database.MealEntity
import com.example.assignment2.data.network.FoodItem
import com.example.assignment2.data.network.RetrofitInstance
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.time.LocalDate

class MealViewModel(application: Application) : AndroidViewModel(application) {
    private val mealDao: MealDao = AppDatabase.getDatabase(application).mealDao()
    private val meals = mutableStateListOf<MealEntity>()
    var totalCalories by mutableStateOf(0.0)
    val dailyGoal = 2000.0 // Predefined daily goal


    var searchResults by mutableStateOf<List<FoodItem>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Manual entry state
    var manualEntryEvent by mutableStateOf<ManualEntryEvent?>(null)

    init {
        loadMeals()
    }

    // Function to search for food items
    fun searchFood(query: String) {
        isLoading = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getNutritionInfo(query)
                searchResults = response.items
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "An error occurred: ${e.localizedMessage}"
                searchResults = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    fun getMealDao(): MealDao {
        return mealDao
    }

    // Function to add searched food item to meals
    fun addFoodItemAsMeal(foodItem: FoodItem, portionSize: Double, mealType: MealType, photoUrl: String?) {
        Log.d("MealViewModel", "Adding food item as meal: $foodItem, $photoUrl")
        val factor = portionSize / 100.0
        val meal = Meal(
            foodName = foodItem.name,
            portionSize = portionSize,
            mealType = mealType,
            calories = foodItem.calories * factor,
            fats = foodItem.fatTotal * factor,
            proteins = foodItem.protein * factor,
            carbohydrates = foodItem.carbohydrates * factor,
            photoUrl = photoUrl
        )
        addMeal(meal.toMealEntity())
    }

    private fun loadMeals() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            val mealEntities = mealDao.getMealsByDate(today)
            val mealList = mealEntities.map { it }
            meals.addAll(mealList)
            totalCalories = mealList.sumOf { it.calories }
        }
    }

    fun deleteMeal(meal: MealEntity) {
        viewModelScope.launch {
            mealDao.deleteMeal(meal)
            meals.remove(meal)
            totalCalories -= meal.calories
        }
    }

    fun updateMeal(meal: MealEntity) {
        viewModelScope.launch {
            mealDao.updateMeal(meal)
            loadMeals()
        }
    }

    fun addMeal(meal: MealEntity) {
        meals.add(meal)
        totalCalories += meal.calories
        saveMeal(meal)
    }

    private fun saveMeal(meal: MealEntity) {
        viewModelScope.launch {
            mealDao.insertMeal(meal)
        }
    }

    fun Meal.toMealEntity(): MealEntity {
        return MealEntity(
            id = id,
            foodName = foodName,
            portionSize = portionSize,
            mealType = mealType.name,
            calories = calories,
            fats = fats,
            proteins = proteins,
            carbohydrates = carbohydrates,
            date = date.toString(),
            photoUrl = photoUrl
        )
    }

    fun MealEntity.toMeal(): Meal {
        return Meal(
            id = id,
            foodName = foodName,
            portionSize = portionSize,
            mealType = MealType.valueOf(mealType),
            calories = calories,
            fats = fats,
            proteins = proteins,
            carbohydrates = carbohydrates,
            date = LocalDate.parse(date),
            photoUrl = photoUrl
        )
    }


    fun promptManualEntry(
        foodName: String,
        portionSize: Double,
        mealType: MealType,
        photoUrl: String? = null
    ) {
        manualEntryEvent = ManualEntryEvent(foodName, portionSize, mealType, photoUrl)
    }

    // Function to handle manual entry submission
    fun submitManualEntry(
        calories: Double,
        fats: Double,
        proteins: Double,
        carbohydrates: Double,
        photoUrl: String?
    ) {
        val event = manualEntryEvent
        if (event != null) {
            val meal = Meal(
                foodName = event.foodName,
                portionSize = event.portionSize,
                mealType = event.mealType,
                calories = calories,
                fats = fats,
                proteins = proteins,
                carbohydrates = carbohydrates,
                photoUrl = photoUrl
            )
            addMeal(meal.toMealEntity())
            // Reset the event
            manualEntryEvent = null
        }
    }

    // Function to cancel manual entry
    fun cancelManualEntry() {
        manualEntryEvent = null
    }

    fun uploadPhotoToFirebase(
        photoData: ByteArray?,
        onProgress: (Float) -> Unit,
        onComplete: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (photoData == null) return

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")

        val uploadTask = imageRef.putBytes(photoData)

        uploadTask.addOnProgressListener { taskSnapshot ->
            Log.d("Firebase", "Progress: ${taskSnapshot.bytesTransferred}/${taskSnapshot.totalByteCount}")
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            onProgress(progress.toFloat() / 100)
        }.addOnSuccessListener { taskSnapshot ->
            // Get the download URL and call onComplete
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                Log.d("Firebase", "Image uploaded successfully: $uri")
                onComplete(uri.toString())
            }
        }.addOnFailureListener { exception ->
            // Handle unsuccessful uploads
            onError(exception)
        }
    }

    fun downloadImageFromFirebase(
        imageUrl: String,
        onImageDownloaded: (Bitmap?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)

        // Specify maximum image size to download
        val ONE_MEGABYTE: Long = 1024 * 1024

        storageRef.getBytes(ONE_MEGABYTE)
            .addOnSuccessListener { bytes ->
                // Convert the ByteArray to Bitmap
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                onImageDownloaded(bitmap) // Pass the bitmap to the callback
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                onError(exception)
            }
    }


}
