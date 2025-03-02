package com.cricketapp.hackfusion.Complaint

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cricketapp.hackfusion.databinding.ActivityComplaintFormBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class complaint_form : AppCompatActivity() {

    private lateinit var binding: ActivityComplaintFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Initialize ViewBinding
        binding = ActivityComplaintFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Initialize Spinner Items
        val facilities = arrayOf("Select Section", "Hostel Section", "Examination Section", "Student Section")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, facilities)
        binding.spinnerFacility.adapter = adapter

        // ✅ Handle Complaint Submission
        binding.btnSubmitComplaint.setOnClickListener {
            submitComplaint()
        }
    }

    private fun submitComplaint() {
        val database = FirebaseDatabase.getInstance().getReference("Complaints")

        // Generate unique complaint ID
        val complaintId = database.push().key ?: ""

        // Get current date and time
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Get user input
        val selectedSection = binding.spinnerFacility.selectedItem.toString()
        val complaintDetails = binding.etComplaintDetails.text.toString()

        // Validation: Ensure user selects a section and enters details
        if (selectedSection == "Select Section") {
            Toast.makeText(this, "Please select a valid section!", Toast.LENGTH_SHORT).show()
            return
        }

        if (complaintDetails.isBlank()) {
            Toast.makeText(this, "Please enter complaint details!", Toast.LENGTH_SHORT).show()
            return
        }

        // Create Complaint object
        val complaint = Complaint(
            id = complaintId,
            section = selectedSection,
            details = complaintDetails,
            timestamp = timestamp
        )

        // Save to Firebase
        database.child(complaintId).setValue(complaint)
            .addOnSuccessListener {
                Toast.makeText(this, "Complaint submitted successfully!", Toast.LENGTH_SHORT).show()
                finish() // Close activity after submission
            }
            .addOnFailureListener {
                Toast.makeText(this, "Complaint submission failed!", Toast.LENGTH_SHORT).show()
            }
    }
}