package com.example.fitnesstrackerapp


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstrackerapp.databinding.ItemAddedfoodBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class HomeFoodAdapter(private var foodDetails: MutableList<FoodDetails> , private val date: String) :
    RecyclerView.Adapter<HomeFoodAdapter.FoodViewHolder>() {

    class FoodViewHolder(private val binding: ItemAddedfoodBinding) :
        RecyclerView.ViewHolder(binding.root) {



        fun bind(foodDetails: FoodDetails, date: String, deleteCallback: (FoodDetails) -> Unit) {

            binding.textViewFoodNameHome.text = foodDetails.name

            // calories (Energy)
            val calories = foodDetails.enrgy
            binding.textViewCaloriesHome.text = calories.let { "calories $calories" } ?: "N/A"

            // protein
            val protein = foodDetails.protien
            binding.textViewProtienHome.text = protein.let { "Protein $protein" } ?: "N/A"

            // carb
            val carb = foodDetails.carb
            binding.textViewCarbHome.text = carb.let { "carb $carb" } ?: "N/A"

            // fats
            val fats = foodDetails.fats
            binding.textViewFatHome.text = fats.let { "fats $fats" } ?: "N/A"

            binding.deleteFoodHome.setOnClickListener {
                deleteCallback(foodDetails)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemAddedfoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return foodDetails.size // Return the size of the food list
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val foodItem = foodDetails[position]
        holder.bind(foodItem , date) { foodToDelete ->
            deleteFoodItem(foodToDelete, position)
        }
    }

    private fun deleteFoodItem(foodToDelete: FoodDetails, position: Int) {
        // Delete item from Firestore
        val db = FirebaseFirestore.getInstance()
        val uid = Firebase.auth.currentUser?.uid
        db.collection("AddFood").whereEqualTo("user uid", uid)
            .whereEqualTo("name", foodToDelete.name)
            .whereEqualTo("date",date)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    // Delete each document
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.d("Firestore", "Document ${document.id} successfully deleted!")
                            // Remove the item from the list and notify the adapter
                            foodDetails.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, foodDetails.size)
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error deleting document ${document.id}", e)
                        }
                }
            }
    }


}