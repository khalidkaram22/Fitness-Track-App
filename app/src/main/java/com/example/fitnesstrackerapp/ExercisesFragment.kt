package com.example.fitnesstrackerapp

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*

class ExercisesFragment : Fragment(), SensorEventListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var isTracking = false
    private var distance = 0.0
    private var previousLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exercises, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val startButton = view.findViewById<Button>(R.id.startButton)
        val distanceText = view.findViewById<TextView>(R.id.distanceText)

        startButton.setOnClickListener {
            if (isTracking) {
                stopTracking()
            } else {
                startTracking()
            }
        }

        requestLocationPermission()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    // Log the received location
                    Log.d(
                        "LocationUpdate",
                        "New Location: ${location.latitude}, ${location.longitude}"
                    )
                    updateDistance(location)
                }
            }
        }

        updateUI(distanceText)
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            // Permission granted
        }
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun startTracking() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        isTracking = true
        view?.findViewById<Button>(R.id.startButton)?.text = "Pause"

        val locationRequest = LocationRequest.create().apply {
            interval = 1000L // Set desired interval for active location updates
            fastestInterval = 500L // Set fastest interval for location updates
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Use high accuracy
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun stopTracking() {
        isTracking = false
        view?.findViewById<Button>(R.id.startButton)?.text = "Start"
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun updateDistance(newLocation: Location) {
        if (previousLocation != null) {
            val distanceBetween = previousLocation!!.distanceTo(newLocation)
            if (distanceBetween > 5) { // Update only if moved more than 5 meters
                distance += distanceBetween / 1000.0 // Convert to kilometers
                updateUI(view?.findViewById(R.id.distanceText)!!)
            }
        }
        previousLocation = newLocation
    }

    private fun updateUI(distanceText: TextView) {
        distanceText.text = "Distance: %.2f km".format(distance)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        TODO("Not yet implemented")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }
}


