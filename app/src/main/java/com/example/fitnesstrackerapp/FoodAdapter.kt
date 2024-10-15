package com.example.fitnesstrackerapp


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.NOTIFICATION_SERVICE
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnesstrackerapp.databinding.FragmentSearchBinding
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

        private val CHANNEL_ID = "fitness_goals_channel"
        private val NOTIFICATION_ID = 1001

        fun bind(foodItem: FoodItem) {
            binding.textViewFoodName.text = foodItem.description

//            Log.d("trace", "foodItem_adapter : $foodItem")

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
                    "fats" to fats?.amount.toString(),
                    "user uid" to uid
                )

                if (uid != null) {
                    db.collection("AddFood")
                        .document()
                        .set(food)
                        .addOnSuccessListener {
                            // Successfully added details
//                            sendNotification()
                            Log.d("Firestore", "food added")
                        }
                        .addOnFailureListener { e ->
                            // Error adding details
                            Log.w("Firestore", "Error writing document", e)
                        }
                }

            }



//            createNotificationChannel()

        }

        // Create the NotificationChannel (required for Android 8+)
//        private fun createNotificationChannel() {
//            // Only create the notification channel on Android 8+ (API 26+)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val name = "Fitness Goals"
//                val descriptionText = "Reminders to check your fitness goals"
//                val importance = NotificationManager.IMPORTANCE_DEFAULT
//                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                    description = descriptionText
//                }
//                // Register the channel with the system
//                val context = SearchFragment().requireContext()
//                val notificationManager: NotificationManager =
//                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                notificationManager.createNotificationChannel(channel)
//            }
//        }
//
//        private fun sendNotification() {
//            // Build the notification
//            val context = SearchFragment().requireContext()
//            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_notifications) // Use your app icon or notification icon here
//                .setContentTitle("Fitness App")
//                .setContentText("your food is added")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//            // Send the notification
//            with(NotificationManagerCompat.from(context)) {
//                notify(NOTIFICATION_ID, builder.build())
//            }
//        }

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