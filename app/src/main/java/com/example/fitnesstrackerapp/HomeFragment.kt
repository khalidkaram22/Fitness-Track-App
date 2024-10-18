package com.example.fitnesstrackerapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnesstrackerapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.TimeZone

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    private lateinit var adapter: HomeFoodAdapter
    private var foodList: MutableList<FoodDetails> = mutableListOf()
    private var calories : Double = 0.0
    private val viewModel: DateViewModel by activityViewModels()
    private lateinit var db: FirebaseFirestore
    private val dateModel: DateViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchAddedFood()

        val date = dateModel.dateLiveData.value.toString()

        // Set up the RecyclerView
        adapter = HomeFoodAdapter(foodList, date)
        binding.recyclerViewHome.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHome.adapter = adapter


        // Observe the date and update the UI when it changes
        viewModel.dateLiveData.observe(viewLifecycleOwner) { newDate ->
            binding.date.text = newDate

        }

        // Handle back arrow click
        binding.dateBackArrow.setOnClickListener {
            viewModel.moveDateBackward()

            fetchAddedFood()
        }

        // Handle forward arrow click
        binding.dateForwardArrow.setOnClickListener {
            viewModel.moveDateForward()

            fetchAddedFood()
        }

        // nav to search
        binding.goSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    fun fetchAddedFood() {
        db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val date = dateModel.dateLiveData.value.toString()
        if (uid != null) {
            // Reset the food list and calories before fetching new data
            foodList.clear()
            calories = 0.0
            binding.totalCalories.text = calories.toString() // Display reset total calories

            val documentReference = db.collection("AddFood")
                .whereEqualTo("user uid", uid)
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener { documents ->
                    documents.forEach {
                        foodList.add(
                            FoodDetails(
                                enrgy = it.getString("enrgy").toString(),
                                name = it.getString("name").toString(),
                                carb = it.getString("carb").toString(),
                                protien = it.getString("protien").toString(),
                                fats = it.getString("fats").toString(),
                                uid = it.getString("user uid").toString()
                            )
                        )
                        val cal = it.getString("enrgy")
                        calories += cal?.toDouble() ?: 0.0 // Accumulate calories
                        Log.d("Firestore", "${it.id} (foodList) => ${foodList}")
                    }

                    // Update the total calories after processing all documents
                    binding.totalCalories.text = calories.toString()
                    adapter.notifyDataSetChanged() // Notify adapter to refresh data after adding all items


                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error getting food: ", exception)
                }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}



