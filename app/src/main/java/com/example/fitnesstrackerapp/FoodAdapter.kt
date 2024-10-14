package com.example.fitnesstrackerapp


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnesstrackerapp.databinding.ItemFoodBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class FoodAdapter(private val foodList: List<FoodItem>) :
    RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {


    class FoodViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var db: FirebaseFirestore
        private val user = Firebase.auth.currentUser

        fun bind(foodItem: FoodItem) {
            binding.textViewFoodName.text = foodItem.description

            Log.d("trace", "foodItem_adapter : $foodItem")

//            val calories = foodItem.foodNutrients.find { it.name == "Energy" }?.amount ?: 0.0

            val calories = foodItem.foodNutrients.find { it.name == "Energy" }

            val amount = calories?.amount.toString()
            val unitname = calories?.unitName.toString()

            binding.textViewCalories.text = calories?.let { "calories $amount $unitname" } ?: "N/A"


            // protien

            val protein = foodItem.foodNutrients.find { it.name == "Protein" }

            val proteinAmount = protein?.amount.toString()
            val proteinUnitName = protein?.unitName.toString()

            binding.textViewProtien.text =
                calories?.let { "Protein $proteinAmount $proteinUnitName" } ?: "N/A"

            // carb

            val carb = foodItem.foodNutrients.find { it.name == "Carbohydrate, by difference" }

            val carbAmount = carb?.amount.toString()
            val carbUnitName = carb?.unitName.toString()

            binding.textViewCarb.text = calories?.let { "carb $carbAmount $carbUnitName" } ?: "N/A"

            // fats

            val fats = foodItem.foodNutrients.find { it.name == "Total lipid (fat)" }

            val fatAmount = fats?.amount.toString()
            val fatUnitName = fats?.unitName.toString()

            binding.textViewFat.text = calories?.let { "fats $fatAmount $fatUnitName" } ?: "N/A"

            //  ${it.amount} ${it.unitName}


            binding.addFood.setOnClickListener {
                db = FirebaseFirestore.getInstance()
                val uid = user?.uid

                val food = hashMapOf(
                    "name" to foodItem.description,
                    "enrgy" to calories?.amount.toString(),
                    "protien" to protein?.amount.toString(),
                    "carb" to carb?.amount.toString(),
                    "fats" to fats?.amount.toString()
                )

                    if (uid != null) {
                        db.collection("AddFood")
                            .document(uid)
                            .set(food)
                            .addOnSuccessListener {
                                // Successfully added details
                                Log.d("Firestore", "food added")
                            }
                            .addOnFailureListener { e ->
                                // Error adding details
                                Log.w("Firestore", "Error writing document", e)
                            }
                    }

            }

            // Default image if no URL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foodList[position])
    }

    override fun getItemCount(): Int {
        return foodList.size
    }
}
