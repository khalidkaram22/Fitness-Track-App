package com.example.fitnesstrackerapp

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(requireContext(), "logout", Toast.LENGTH_SHORT).show()

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