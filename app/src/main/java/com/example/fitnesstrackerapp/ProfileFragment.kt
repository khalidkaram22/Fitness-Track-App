package com.example.fitnesstrackerapp

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.edit
import androidx.core.view.isVisible
import com.example.fitnesstrackerapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore
    private val user = Firebase.auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         db = FirebaseFirestore.getInstance()
         val uid = FirebaseAuth.getInstance().currentUser?.uid

        binding.applyEditBtn.setOnClickListener {
            val userName = binding.profileNameEt.text.toString()
            val height = binding.profileHeightEt.text.toString()
            val weight = binding.profileWeightEt.text.toString()
            val age = binding.profileAgeEt.text.toString()

            if (uid != null) {

                val userDetails = UserDetails(
                    uid = uid,
                    email = user?.email.toString(),
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
                        Log.d("Firestore", "User details successfully changed!")
                    }
                    .addOnFailureListener { e ->
                        // Error adding details
                        Log.w("Firestore", "Error writing document", e)
                    }


                Toast.makeText(requireContext(),"changed succsefully", Toast.LENGTH_SHORT).show()
            }

        }



        if (uid != null) {
            val docRef = db.collection("usersDetails").document(uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userDetails = document.toObject(UserDetails::class.java)

                        userDetails?.let {
                            // Update UI with user details
                            binding.profileHeightEt.setText(it.height)
                            binding.profileWeightEt.setText(it.weight)
                            binding.profileAgeEt.setText(it.age)
                            binding.profileNameEt.setText(it.userName)
                            binding.emailSpace.text = it.email
                        }
                    } else {
                        Log.d("Firestore", "No such document")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error getting document", e)
                }
        }



        // logout with SharedPreferences
        binding.logoutBtn.setOnClickListener {
            val sharedPref =
                requireActivity().getSharedPreferences("login_prefs", MODE_PRIVATE).edit() {
                    putString("email", "")
                    putString("password", "")
                    putBoolean("remember_me", false)
                }
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            requireActivity().finish()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}