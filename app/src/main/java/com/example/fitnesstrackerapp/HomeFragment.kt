package com.example.fitnesstrackerapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnesstrackerapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HomeFoodAdapter
    private var foodList: MutableList<FoodDetails> = mutableListOf()
    private var calories : Double = 0.0

    private lateinit var db: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid


        // Set up the RecyclerView
        adapter = HomeFoodAdapter(foodList)
        Log.d("trace", "foodList : $foodList")
        binding.recyclerViewHome.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHome.adapter = adapter
        binding.totalCalories.text=calories.toString()


        if (uid != null) {
            val documentReference = db.collection("AddFood")
                .whereEqualTo("user uid", uid)
                .get()
                .addOnSuccessListener { documents ->
                    documents.forEach {
                        foodList.add(
                            FoodDetails(
                                enrgy = it.getString("enrgy").toString(),
                                name = it.getString("name").toString(),  // Ensure there are no extra spaces in keys
                                carb = it.getString("carb").toString(),
                                protien = it.getString("protien").toString(),
                                fats = it.getString("fats").toString(),
                                uid = it.getString("user uid").toString()
                            )
                        )
                        val cal =it.getString("enrgy")
                        calories+= cal?.toDouble() ?: 0.0
                        binding.totalCalories.text=calories.toString()
                        Log.d("Firestore", "${it.id} (foodList) => ${foodList}")
                    }
                    adapter.notifyDataSetChanged() // Notify adapter to refresh data after adding all items
                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error getting food: ", exception)
                }
        }



        binding.goSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}



