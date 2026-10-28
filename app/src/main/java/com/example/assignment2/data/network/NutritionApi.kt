package com.example.assignment2.data.network

import com.example.assignment2.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface NutritionApi {
    @GET("nutrition")
    suspend fun getNutritionInfo(@Query("query") query: String): NutritionResponse
}


object RetrofitInstance {
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-Api-Key", BuildConfig.CALORIE_NINJAS_API_KEY)
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.calorieninjas.com/v1/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: NutritionApi = retrofit.create(NutritionApi::class.java)
}

