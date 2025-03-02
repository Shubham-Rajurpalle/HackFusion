package com.cricketapp.hackfusion.ConductVoilate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cricketapp.hackfusion.Home.FacultyHomeFragment
import com.cricketapp.hackfusion.R
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

class FillCaughtDetails : AppCompatActivity() {

    private lateinit var tvStudentName: TextView
    private lateinit var tvRegNo: TextView
    private lateinit var etReason: EditText
    private lateinit var btnSubmit: Button
    private lateinit var db: FirebaseFirestore

    private var scannedData: String? = null
    private var studentName: String? = null
    private var studentRegNo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_caught_details)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Get Views
        tvStudentName = findViewById(R.id.tvStudentName)
        tvRegNo = findViewById(R.id.tvRegNo)
        etReason = findViewById(R.id.etReason)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Get Scanned Data from Intent
        scannedData = intent.getStringExtra("QR_CODE_DATA")

        if (scannedData.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid QR Code Data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Parse and Fill Student Details
        parseAndFillStudentDetails(scannedData!!)

        // Submit Button Click
        btnSubmit.setOnClickListener {
            submitCaughtStudentData()
        }
    }

    private fun parseAndFillStudentDetails(qrData: String) {
        try {
            val jsonObject = JSONObject(qrData) // Convert string to JSON
            studentName = jsonObject.getString("name")
            studentRegNo = jsonObject.getString("reg_no")

            // Set values to UI
            tvStudentName.text = "Name: $studentName"
            tvRegNo.text = "Reg No: $studentRegNo"

        } catch (e: Exception) {
            Log.e("FillCaughtDetails", "Error parsing QR code JSON", e)
            Toast.makeText(this, "Error reading QR data", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun submitCaughtStudentData() {
        val reason = etReason.text.toString().trim()
        if (reason.isEmpty()) {
            Toast.makeText(this, "Please enter a reason", Toast.LENGTH_SHORT).show()
            return
        }

        val studentData = hashMapOf(
            "name" to studentName,
            "reg_no" to studentRegNo,
            "reason" to reason,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("studentsCaught")
            .add(studentData)
            .addOnSuccessListener {
                Toast.makeText(this, "Student Caught Successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, FacultyHomeFragment::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}