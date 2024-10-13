package com.example.fitnesstrackerapp

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle

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



class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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
        Toast.makeText(requireContext(), "logout", Toast.LENGTH_SHORT).show()


        binding.ageEt.setOnClickListener {
//            binding.ageEt.isEnabled= true
            binding.editBtn.isVisible = true

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