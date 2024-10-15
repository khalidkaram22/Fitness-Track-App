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


data class FoodDetails(
    val carb: String,
    val fats: String,
    val enrgy: String,
    val uid: String,
    val name: String,
    val protien: String,
)