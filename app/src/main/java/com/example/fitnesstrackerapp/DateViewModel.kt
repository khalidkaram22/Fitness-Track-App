package com.example.fitnesstrackerapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar
import java.util.TimeZone

class DateViewModel: ViewModel() {

    private val calendar = Calendar.getInstance(TimeZone.getDefault())
    val dateLiveData = MutableLiveData<String>()

    init {
        updateDate() // Initialize the date when the ViewModel is created
    }

    // Function to update date based on the current state of the calendar
    fun updateDate() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Months are 0-based
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val date = "$day/$month/$year"
        dateLiveData.value = date
    }

    // Function to move the date backward
    fun moveDateBackward() {
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        updateDate()
    }

    // Function to move the date forward
    fun moveDateForward() {
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        updateDate()
    }

}

