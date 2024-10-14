package com.example.fitnesstrackerapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gotoLogin.setOnClickListener {
            it.findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
        auth = Firebase.auth

        binding.signupBtn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passwordEt.text.toString()
            val conPass = binding.conPassEt.text.toString()

            if (email.isBlank() || pass.isBlank() || conPass.isBlank())
                Toast.makeText(requireContext(), "missed field", Toast.LENGTH_SHORT).show()
            else if (pass.length < 6)
                Toast.makeText(requireContext(), "Short Password!", Toast.LENGTH_SHORT).show()
            else if (pass != conPass)
                Toast.makeText(requireContext(), "Passwords don't match", Toast.LENGTH_SHORT).show()
            else {
                signUpUser(email, pass)
            }
        }
    }

    private fun signUpUser(email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    verifyEmail()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun verifyEmail() {
        val user = Firebase.auth.currentUser

        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Verification email sent. Please check your email.", Toast.LENGTH_SHORT).show()

                    // Now start waiting for the user to verify their email
                    waitForEmailVerification()
                } else {
                    Toast.makeText(requireContext(), "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun waitForEmailVerification() {
        val user = Firebase.auth.currentUser

        // Handler to run periodic checks
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                user?.reload()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (user.isEmailVerified) {
                            Toast.makeText(requireContext(), "Email verified!", Toast.LENGTH_SHORT).show()

                            // Navigate to the next fragment after email verification
                            findNavController().navigate(R.id.action_signUpFragment_to_userDetailsFragment)
                        } else {
                            // If email not verified yet, check again after 5 seconds
                            handler.postDelayed(this, 5000)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to refresh user data.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Start checking for email verification
        handler.post(runnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
