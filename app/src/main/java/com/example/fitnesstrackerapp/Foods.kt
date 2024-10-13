package com.example.fitnesstrackerapp

data class Foods(
    val foods: List<FoodItem>
)


data class FoodItem(
    val description: String,
    val foodNutrients: List<FoodNutrient>,
)

data class FoodNutrient(
    val name: String,
    val amount: Double,
    val unitName: String
)
