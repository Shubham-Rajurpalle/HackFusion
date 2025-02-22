package com.cricketapp.hackfusion

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class booking_facility : AppCompatActivity() {

    private lateinit var spinnerFacility: Spinner
    private lateinit var btnStartTime: Button
    private lateinit var btnEndTime: Button
    private lateinit var btnSubmit: Button
    private lateinit var etPurpose: EditText
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_facility)

        // Initialize UI Components
        spinnerFacility = findViewById(R.id.spinnerFacility)
        btnStartTime = findViewById(R.id.btnStartTime)
        btnEndTime = findViewById(R.id.btnEndTime)
        btnSubmit = findViewById(R.id.btnSubmit)
        etPurpose = findViewById(R.id.etPurpose)

        database = FirebaseDatabase.getInstance().reference

        // Setup Spinner (Dropdown)
        val facilities = arrayOf("Select Facility", "Auditorium", "Ground", "Lab")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, facilities)
        spinnerFacility.adapter = adapter

        // Time Pickers for Start and End Time
        btnStartTime.setOnClickListener { showTimePicker(btnStartTime, true) }
        btnEndTime.setOnClickListener { showTimePicker(btnEndTime, false) }

        // Submit Button Click
        btnSubmit.setOnClickListener {
            Log.d("Booking", "Submit button clicked")

            val selectedFacility = spinnerFacility.selectedItem.toString()
            val purpose = etPurpose.text.toString().trim()
            val startTime = btnStartTime.text.toString().trim()
            val endTime = btnEndTime.text.toString().trim()

            if (selectedFacility == "Select Facility" || purpose.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Log.e("Booking", "Validation failed: Missing required fields")
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check for conflicts before proceeding
            isFacilityAvailable(selectedFacility, startTime, endTime) { available ->
                if (available) {
                    getCurrentUserName { userName ->
                        Log.d("Booking", "Fetched user name: $userName")

                        val bookingId = database.child("Bookings").push().key  // Generate unique ID
                        if (bookingId == null) {
                            Log.e("Booking", "Failed to generate booking ID")
                            Toast.makeText(this, "Error booking. Try again!", Toast.LENGTH_SHORT).show()
                            return@getCurrentUserName
                        }

                        // Create Booking Object
                        val booking = Booking(
                            facility = selectedFacility,
                            purpose = purpose,
                            startTime = startTime,
                            endTime = endTime,
                            approved = false,  // Default: Not Approved
                            bookedBy = userName
                        )

                        Log.d("Booking", "Submitting booking: $bookingId | Facility: $selectedFacility | User: $userName")

                        database.child("Bookings").child(bookingId).setValue(booking)
                            .addOnSuccessListener {
                                Log.d("Booking", "Booking saved successfully: $bookingId")
                                Toast.makeText(this, "Booking Successful!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Booking", "Error saving booking: ${exception.message}")
                                Toast.makeText(this, "Booking failed. Try again!", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Facility is already booked at this time!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Fetch Current User Name from Realtime Database
    private fun getCurrentUserName(callback: (String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            callback("Unknown User")
            return
        }

        val userRef = database.child("Users").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java) ?: "Unknown User"
                callback(name)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching user name: ${error.message}")
                callback("Unknown User")
            }
        })
    }

    // Function to check if the facility is already booked
    private fun isFacilityAvailable(facility: String, startTime: String, endTime: String, callback: (Boolean) -> Unit) {
        val databaseRef = database.child("Bookings")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (bookingSnapshot in snapshot.children) {
                    val bookedFacility = bookingSnapshot.child("facility").getValue(String::class.java) ?: continue
                    val existingStartTime = bookingSnapshot.child("startTime").getValue(String::class.java) ?: continue
                    val existingEndTime = bookingSnapshot.child("endTime").getValue(String::class.java) ?: continue

                    if (bookedFacility == facility &&
                        ((startTime >= existingStartTime && startTime < existingEndTime) ||
                                (endTime > existingStartTime && endTime <= existingEndTime) ||
                                (startTime <= existingStartTime && endTime >= existingEndTime))) {

                        callback(false) // Conflict found
                        return
                    }
                }
                callback(true) // No conflict, proceed with booking
            }

            override fun onCancelled(error: DatabaseError) {
                callback(true) // Assume no conflict in case of failure
            }
        })
    }

    // Function to show Time Picker and ensure valid time selection
    private fun showTimePicker(button: Button, isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)

            if (!isStartTime) {
                val startTime = btnStartTime.text.toString()
                if (startTime.isNotEmpty() && selectedTime <= startTime) {
                    Toast.makeText(this, "End time must be after start time!", Toast.LENGTH_SHORT).show()
                    return@TimePickerDialog
                }
            }
            button.text = selectedTime
        }, hour, minute, false)

        timePickerDialog.show()
    }
}