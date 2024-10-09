package com.example.fitnesstrackerapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.fitnesstrackerapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflate the layout using View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find the NavHostFragment and NavController
        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        // Setup BottomNavigationView with NavController
        binding.bottomNav.setupWithNavController(navController)


        // call onOptionsItemSelected
        binding.bottomNav.setOnItemSelectedListener { i ->
            onOptionsItemSelected(i)
        }


    }

    // Handle menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_profile -> {
                // Navigate to Profile Fragment
                Toast.makeText(this, "hello profile", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.profileFragment)
                true
            }

            R.id.nav_home -> {
                // Navigate to Profile Fragment
                Toast.makeText(this, "hello home", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.homeFragment)
                true
            }

            R.id.nav_exercise -> {
                // Navigate to Profile Fragment
                Toast.makeText(this, "hello home", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.exercisesFragment)
                true
            }
            R.id.nav_chart -> {
                // Navigate to Profile Fragment
                Toast.makeText(this, "hello home", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.chartFragment)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
        // Check if there is a fragment in the back stack to pop
        if (!navController.popBackStack()) {
            // If the back stack is empty, exit the app
            super.onBackPressed()
        }
    }


}
