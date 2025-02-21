package com.cricketapp.hackfusion

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class booking_facility : AppCompatActivity() {

    private lateinit var spinnerFacility: Spinner
    private lateinit var btnStartTime: Button
    private lateinit var btnEndTime: Button
    private lateinit var btnSubmit: Button
    private lateinit var etPurpose: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_facility)

        // Initialize UI Components
        spinnerFacility = findViewById(R.id.spinnerFacility)
        btnStartTime = findViewById(R.id.btnStartTime)
        btnEndTime = findViewById(R.id.btnEndTime)
        btnSubmit = findViewById(R.id.btnSubmit)
        etPurpose = findViewById(R.id.etPurpose)

        // Setup Spinner (Dropdown)
        val facilities = arrayOf("Select Facility", "Auditorium", "Ground", "Lab")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, facilities)
        spinnerFacility.adapter = adapter

        // Time Pickers for Start and End Time
        btnStartTime.setOnClickListener { showTimePicker(btnStartTime) }
        btnEndTime.setOnClickListener { showTimePicker(btnEndTime) }

        // Submit Button Click
        btnSubmit.setOnClickListener {
            val selectedFacility = spinnerFacility.selectedItem.toString()
            val purpose = etPurpose.text.toString()

            if (selectedFacility == "Select Facility" || purpose.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Booking Successful!", Toast.LENGTH_SHORT).show()
                // Handle database submission
            }
        }
    }

    private fun showTimePicker(button: Button) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            button.text = String.format("%02d:%02d", selectedHour, selectedMinute)
        }, hour, minute, false)

        timePickerDialog.show()
    }
}