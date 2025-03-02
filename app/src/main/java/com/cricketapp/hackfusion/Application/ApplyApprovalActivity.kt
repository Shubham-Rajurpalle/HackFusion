package com.cricketapp.hackfusion.Application

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cricketapp.hackfusion.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ApplyApprovalActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etApprovalDetails: EditText
    private lateinit var btnSubmit: Button
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance() // Get current user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_apply_approval) // Ensure layout is set first

        // Find UI elements
        val mainView = findViewById<View>(R.id.main)
        etName = findViewById(R.id.etName)
        etApprovalDetails = findViewById(R.id.etApprovalDetails)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Apply window insets safely
        mainView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // Fetch and set user name
        fetchUserName()

        // Set button click listener for submitting data
        btnSubmit.setOnClickListener {
            submitApprovalRequest()
        }
    }

    private fun fetchUserName() {
        val userId = auth.currentUser?.uid // Get current user ID
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name") ?: "Unknown User"
                        etName.setText(name)
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to fetch name", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitApprovalRequest() {
        val userId = auth.currentUser?.uid
        val name = etName.text.toString()
        val approvalDetails = etApprovalDetails.text.toString()

        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        if (approvalDetails.isEmpty()) {
            Toast.makeText(this, "Please enter approval details", Toast.LENGTH_SHORT).show()
            return
        }

        val approvalRequest = hashMapOf(
            "userId" to userId,
            "name" to name,
            "approvalDetails" to approvalDetails,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("approval_requests")
            .add(approvalRequest)
            .addOnSuccessListener {
                Toast.makeText(this, "Approval Request Submitted Successfully", Toast.LENGTH_SHORT).show()
                etApprovalDetails.text.clear() // Clear the input after submission
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to Submit Request", Toast.LENGTH_SHORT).show()
            }
    }
}