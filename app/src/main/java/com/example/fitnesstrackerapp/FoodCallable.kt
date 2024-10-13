package com.example.fitnesstrackerapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


//   https://api.nal.usda.gov/fdc/v1/foods/list?api_key=YqOgVlIserssGg1iNNkz7LZcdW8WK3xzJn711zTc
//   YqOgVlIserssGg1iNNkz7LZcdW8WK3xzJn711zTc ->api_key


interface FoodCallable {
    @GET("v1/foods/list")
    fun searchFoods(
        @Query("api_key") apiKey: String
    ): Call<List<FoodItem>> // Changed from Call<Foods> to Call<List<FoodItem>>
}





