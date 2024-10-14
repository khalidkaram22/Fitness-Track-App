package com.example.fitnesstrackerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.databinding.FragmentSignUpBinding
import com.example.fitnesstrackerapp.databinding.FragmentUserDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class UserDetailsFragment : Fragment() {

    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()





        binding.skipBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }

        binding.saveDetailsBnt.setOnClickListener {
            val userName = binding.nameEt.text.toString()
            val height = binding.heightEt.text.toString()
            val weight = binding.weightEt.text.toString()
            val age = binding.ageEt.text.toString()

            val user = Firebase.auth.currentUser

            // Reload user data to ensure the latest email verification status is obtained
            if (user != null) {

                if (user.isEmailVerified) {
                    // Action to be performed when email is verified
                    saveUserDetails(userName ,height, weight, age)
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                } else {
                    Toast.makeText(context, "Please verify your email.", Toast.LENGTH_SHORT).show()
                    // findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                }
            } else {
                Toast.makeText(context, "Please verify your email or signup your email.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserDetails(userName:String,height: String, weight: String, age: String) {

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {

            val userDetails = UserDetails(
                uid = uid,
                email = user.email.toString(),
                userName= userName,
                age = age,
                height = height,
                weight = weight
            )

            db.collection("usersDetails")
                .document(uid)
                .set(userDetails)
                .addOnSuccessListener {
                    // Successfully added details
                    Log.d("Firestore", "User details successfully written!")
                }
                .addOnFailureListener { e ->
                    // Error adding details
                    Log.w("Firestore", "Error writing document", e)
                }
        }

    }

}