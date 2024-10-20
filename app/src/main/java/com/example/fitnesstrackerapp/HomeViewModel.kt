package com.example.fitnesstrackerapp


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {

    private val _foodDetails = MutableLiveData<MutableList<FoodDetails>>()
    val foodDetails: LiveData<MutableList<FoodDetails>> = _foodDetails

    private val _totalCalories = MutableLiveData<Double>()
    val totalCalories: LiveData<Double> = _totalCalories

    private val db = FirebaseFirestore.getInstance()

    fun fetchAddedFood(date: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            db.collection("AddFood")
                .whereEqualTo("user uid", uid)
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener { documents ->
                    val fetchedFoodList = mutableListOf<FoodDetails>()
                    var calories = 0.0

                    documents.forEach {
                        val food = FoodDetails(
                            enrgy = it.getString("enrgy").toString(),
                            name = it.getString("name").toString(),
                            carb = it.getString("carb").toString(),
                            protien = it.getString("protien").toString(),
                            fats = it.getString("fats").toString(),
                            uid = it.getString("user uid").toString()
                        )
                        fetchedFoodList.add(food)

                        val cal = it.getString("enrgy")
                        calories += cal?.toDouble() ?: 0.0
                    }

                    _foodDetails.value = fetchedFoodList
                    _totalCalories.value = calories
                }
        }
    }

    fun setTotalCalories(updatedCalories: Double) {
        _totalCalories.value = updatedCalories
        Log.d("trace", "in vm cal = $updatedCalories")
    }


}
