package com.example.fitnesstrackerapp
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.fitnesstrackerapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private val CHANNEL_ID = "fitness_goals_channel"
    private val NOTIFICATION_ID = 1001

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

        // Set up item selection for bottom navigation
        binding.bottomNav.setOnItemSelectedListener { i ->
            onOptionsItemSelected(i)
        }

        // Create the notification channel and send a notification
        createNotificationChannel()

    }

    // Create the NotificationChannel (required for Android 8+)
    private fun createNotificationChannel() {
        // Only create the notification channel on Android 8+ (API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Fitness Goals"
            val descriptionText = "Reminders to check your fitness goals"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        // Build the notification
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications) // Use your app icon or notification icon here
            .setContentTitle("Fitness App")
            .setContentText("set your wieght statues")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Send the notification
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    // Handle menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_profile -> {
                // Navigate to Profile Fragment
                navController.navigate(R.id.profileFragment)
                true
            }

            R.id.nav_home -> {
                // Navigate to Home Fragment
                sendNotification()
                navController.navigate(R.id.homeFragment)
                true
            }

            R.id.nav_exercise -> {
                // Navigate to Exercises Fragment
                navController.navigate(R.id.exercisesFragment)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    // Handle back press
    override fun onBackPressed() {
        // Check if there is a fragment in the back stack to pop
        if (!navController.popBackStack()) {
            // If the back stack is empty, exit the app
            super.onBackPressed()
        }
    }


}
