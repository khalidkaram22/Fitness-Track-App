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
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val dateModel: DateViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val date = dateModel.dateLiveData.value.toString()

        // Set up the RecyclerView
        adapter = HomeFoodAdapter(mutableListOf(), date)
        binding.recyclerViewHome.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHome.adapter = adapter


        // Observe LiveData from ViewModel for food details
        homeViewModel.foodDetails.observe(viewLifecycleOwner) { foodDetails ->
            adapter.submitList(foodDetails) // Update the adapter when the data changes
        }

        // Observe LiveData for total calories
        homeViewModel.totalCalories.observe(viewLifecycleOwner) { totalCalories ->
            binding.totalCalories.text = totalCalories.toString() // Update total calories text
        }

        // Fetch data initially
        homeViewModel.fetchAddedFood(date)

        // Observe the date and update the UI when it changes
        dateModel.dateLiveData.observe(viewLifecycleOwner) { newDate ->
            binding.date.text = newDate
            homeViewModel.fetchAddedFood(newDate)
        }

        // Handle back arrow click
        binding.dateBackArrow.setOnClickListener {
            dateModel.moveDateBackward()
            //fetchAddedFood()
        }

        // Handle forward arrow click
        binding.dateForwardArrow.setOnClickListener {
            dateModel.moveDateForward()
            //fetchAddedFood()
        }

        // nav to search
        binding.goSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        Log.d("trace","cal = ${homeViewModel.totalCalories.value}")
    }

    fun updateTotalCalories(caloriesToRemove: Double) {
        homeViewModel.totalCalories.value?.let { currentValue ->
            val updatedCalories = currentValue - caloriesToRemove
            homeViewModel.setTotalCalories(updatedCalories)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}



