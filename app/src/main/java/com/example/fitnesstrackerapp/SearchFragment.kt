package com.example.fitnesstrackerapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnesstrackerapp.databinding.FragmentSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var foodCall: FoodCallable
    private lateinit var adapter: FoodAdapter
    private var foodList: MutableList<FoodItem> = mutableListOf()
    private val dateModel: DateViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.backToHome.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.nal.usda.gov/fdc/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        foodCall = retrofit.create(FoodCallable::class.java)


        val date =dateModel.dateLiveData.value.toString()

        // Set up the RecyclerView
        adapter = FoodAdapter(foodList,date)
        Log.d("trace", "foodList : $foodList")
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        Log.d("trace", "adapter : $adapter")

        // Set up the search button click listener
        binding.buttonSearch.setOnClickListener {
            val foodName = binding.editTextFood.text.toString()
            if (foodName.isNotBlank()) {
                Log.d("trace", "Searching for: $foodName")
                fetchFoodCalories(foodName)
            } else {
                Toast.makeText(requireContext(), "Please enter a food name", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }


    private fun fetchFoodCalories(foodName: String) {
        val call = foodCall.searchFoods("YqOgVlIserssGg1iNNkz7LZcdW8WK3xzJn711zTc")
        call.enqueue(object : Callback<List<FoodItem>> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<List<FoodItem>>, response: Response<List<FoodItem>>) {
                if (response.isSuccessful) {
                    val items = response.body() ?: emptyList()

                    if (items.isEmpty()) {
                        Toast.makeText(context, "No items found", Toast.LENGTH_SHORT).show()
                    }

                    foodList.clear() // Clear previous items
                    items.forEach { item ->
                        if (item.description==foodName) {
                            foodList.add(FoodItem(item.description, item.foodNutrients))
                            Log.d("trace", "foodlist : $foodList")
                        }
                        else{
                           // Toast.makeText(requireContext(),"this food not found",Toast.LENGTH_SHORT).show()
                        }
                    }
                    adapter.notifyDataSetChanged() // Notify adapter to refresh data

                } else {
                     Toast.makeText(requireContext(),"response is not Successful",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FoodItem>>, t: Throwable) {

            }

        })
    }

}



