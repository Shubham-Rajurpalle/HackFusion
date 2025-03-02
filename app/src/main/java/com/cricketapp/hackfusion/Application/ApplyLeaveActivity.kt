package com.cricketapp.hackfusion.Application

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.cricketapp.hackfusion.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ApplyLeaveActivity : AppCompatActivity() {

    private lateinit var etStartDate: EditText
    private lateinit var etEndDate: EditText
    private lateinit var etName: EditText
    private lateinit var etRollNumber: EditText
    private lateinit var etReason: EditText
    private lateinit var spinnerLeaveType: Spinner
    private lateinit var btnSubmit: Button
    private lateinit var btnCancel: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_leave) // Ensure this is first

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Find views
        etName = findViewById(R.id.etName)
        etRollNumber = findViewById(R.id.etRollNumber)
        etReason = findViewById(R.id.etReason)
        etStartDate = findViewById(R.id.etStartDate)
        etEndDate = findViewById(R.id.etEndDate)
        spinnerLeaveType = findViewById(R.id.spinnerLeaveType)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnCancel = findViewById(R.id.btnCancel)

        val leaveTypes = arrayOf("Sick Leave", "Casual Leave", "Emergency Leave")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, leaveTypes)
        spinnerLeaveType.adapter = adapter

        // Fetch user details
        fetchUserDetails()

        // Set up date picker
        etStartDate.setOnClickListener { showDatePicker(etStartDate) }
        etEndDate.setOnClickListener { showDatePicker(etEndDate) }

        // Submit button click listener
        btnSubmit.setOnClickListener { submitLeaveApplication() }

        // Cancel button click listener (Just closes the activity)
        btnCancel.setOnClickListener { finish() }
    }

    // Show DatePickerDialog when clicking on date fields
    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                // Format the selected date
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                editText.setText(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun fetchUserDetails() {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Log.e("ApplyLeaveActivity", "User not logged in.")
            return
        }

        Log.d("ApplyLeaveActivity", "Fetching data for user ID: $userId")

        val userRef = firestore.collection("users").document(userId)

        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("FirestoreData", "User data found: ${document.data}")

                    val name = document.getString("name") ?: "Unknown"
                    val rollNumber = document.getString("roll_number") ?: "Unknown"

                    etName.setText(name)
                    etRollNumber.setText(rollNumber)

                    // Disable editing
                    etName.isFocusable = false
                    etName.isClickable = false
                    etName.isCursorVisible = false

                    etRollNumber.isFocusable = false
                    etRollNumber.isClickable = false
                    etRollNumber.isCursorVisible = false

                } else {
                    Log.e("FirestoreError", "User document not found for UID: $userId")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error fetching user details: ${exception.message}")
            }
    }

    // Submit leave application data to Firestore
    private fun submitLeaveApplication() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        val name = etName.text.toString().trim()
        val rollNumber = etRollNumber.text.toString().trim()
        val leaveType = spinnerLeaveType.selectedItem.toString()
        val startDate = etStartDate.text.toString().trim()
        val endDate = etEndDate.text.toString().trim()
        val reason = etReason.text.toString().trim()

        // Validate input fields
        if (name.isEmpty() || rollNumber.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            return
        }

        val leaveData = hashMapOf(
            "userId" to userId,
            "name" to name,
            "rollNumber" to rollNumber,
            "leaveType" to leaveType,
            "startDate" to startDate,
            "endDate" to endDate,
            "reason" to reason,
            "status" to "Pending", // Default status
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("leave_applications").add(leaveData)
            .addOnSuccessListener {
                Toast.makeText(this, "Leave application submitted successfully!", Toast.LENGTH_LONG).show()
                finish() // Close activity after submission
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error submitting leave application: ${e.message}")
                Toast.makeText(this, "Error submitting application. Try again.", Toast.LENGTH_SHORT).show()
            }
    }
}