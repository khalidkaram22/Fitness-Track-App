package com.example.fitnesstrackerapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.fitnesstrackerapp.databinding.FragmentExercisesBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.gms.location.*

class ExercisesFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentExercisesBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var isTracking = false
    private var distance = 0.0
    private var previousLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExercisesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.startButton.setOnClickListener {
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
                    Log.d("LocationUpdate", "New Location: ${location.latitude}, ${location.longitude}")
                    updateDistance(location)
                }
            }
        }

        updateUI()
        updatePieChart()
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
        binding.startButton.text = "Pause"

        val locationRequest = LocationRequest.create().apply {
            interval = 1000L // Set desired interval for active location updates
            fastestInterval = 500L // Set fastest interval for location updates
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Use high accuracy
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun stopTracking() {
        isTracking = false
        binding.startButton.text = "Start"
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun updateDistance(newLocation: Location) {
        if (previousLocation != null) {
            val distanceBetween = previousLocation!!.distanceTo(newLocation)
            if (distanceBetween > 5) {
                distance += distanceBetween / 1000.0
                updateUI()
                updatePieChart()
            }
        }
        previousLocation = newLocation
    }

    private fun updatePieChart() {
        val entries = mutableListOf<PieEntry>()
        entries.add(PieEntry(distance.toFloat(), "Distance"))
        entries.add(PieEntry((10 - distance).toFloat(), "Remaining"))

        val dataSet = PieDataSet(entries, "Exercise")
        val customColors = listOf(
            Color.parseColor("#EB3678"), // Pink
            Color.parseColor("#FF7F00") // Orange
        )

        dataSet.colors = customColors

        val pieData = PieData(dataSet)
        binding.pieChart.data = pieData
        binding.pieChart.invalidate() // Refresh the chart
    }

    private fun updateUI() {
        binding.distanceText.text = "Distance: %.2f km".format(distance)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Implement sensor logic here
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Implement accuracy change logic here
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
