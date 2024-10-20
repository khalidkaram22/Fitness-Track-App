package com.example.fitnesstrackerapp


import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstrackerapp.databinding.ItemAddedfoodBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class HomeFoodAdapter(private var foodDetails: MutableList<FoodDetails>, private val date: String) :
    RecyclerView.Adapter<HomeFoodAdapter.FoodViewHolder>() {

    class FoodViewHolder(private val binding: ItemAddedfoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(foodDetails: FoodDetails, date: String, deleteCallback: (FoodDetails) -> Unit) {
            binding.textViewFoodNameHome.text = foodDetails.name
            binding.textViewCaloriesHome.text = "calories ${foodDetails.enrgy}"
            binding.textViewProtienHome.text = "Protein ${foodDetails.protien}"
            binding.textViewCarbHome.text = "carb ${foodDetails.carb}"
            binding.textViewFatHome.text = "fats ${foodDetails.fats}"

            binding.deleteFoodHome.setOnClickListener {
                deleteCallback(foodDetails)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemAddedfoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun getItemCount(): Int = foodDetails.size

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val foodItem = foodDetails[position]
        holder.bind(foodItem, date) { foodToDelete ->
            deleteFoodItem(foodToDelete, position){caloriesRemoved->
                (holder.itemView.context as? HomeFragment)?.updateTotalCalories(caloriesRemoved)

            }
        }
    }

    fun submitList(newFoodList: MutableList<FoodDetails>) {
        foodDetails = newFoodList
        notifyDataSetChanged()
    }

    private fun deleteFoodItem(foodToDelete: FoodDetails, position: Int, deleteCallback: (Double) -> Unit) {
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
                            // Remove the item from the list and notify the adapter and pass the calories_to_remove back

                            val caloriesToRemove = foodToDelete.enrgy.toDouble() ?: 0.0
                            foodDetails.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, foodDetails.size)

                            // Call the delete callback with the removed calories
                            deleteCallback(caloriesToRemove)

                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error deleting document ${document.id}", e)
                        }
                }
            }
    }


}