package com.example.assignment2.data.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// MealEntity for Room
@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val foodName: String,
    val portionSize: Double,
    val mealType: String,
    val calories: Double,
    val fats: Double,
    val proteins: Double,
    val carbohydrates: Double,
    val date: String,
    val photoUrl: String? = null
)



// MealDao Interface
@Dao
interface MealDao {
    @Insert
    suspend fun insertMeal(meal: MealEntity)

    @Query("SELECT * FROM meals")
    fun getAllMeals(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE date = :date")
    suspend fun getMealsByDate(date: String): List<MealEntity>

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Delete
    suspend fun deleteMeal(meal: MealEntity)
}

@Database(entities = [MealEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "contact_photo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}




