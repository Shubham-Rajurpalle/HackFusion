package com.cricketapp.hackfusion

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cricketapp.hackfusion.R
import org.json.JSONObject

class signUp : AppCompatActivity() {

    private lateinit var etRole: EditText
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Views
        etRole = findViewById(R.id.etRole)
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Get QR Data from Intent
        val qrData = intent.getStringExtra("QR_CODE_DATA")

        if (!qrData.isNullOrEmpty()) {
            try {
                // ✅ Convert QR string to JSON
                val jsonData = JSONObject(qrData)

                // ✅ Extract fields safely, replacing "null" with empty string
                val role = jsonData.optString("role", "").takeIf { it != "null" } ?: ""
                val name = jsonData.optString("name", "").takeIf { it != "null" } ?: ""
                val phone = jsonData.optString("self_phone", "").takeIf { it != "null" } ?: ""
                val email = jsonData.optString("self_mail", "").takeIf { it != "null" } ?: ""

                // ✅ Pre-fill EditText fields
                etRole.setText(role)
                etName.setText(name)
                etPhone.setText(phone)
                etEmail.setText(email)

                Log.d("SignUpActivity", "Auto-filled: Role=$role, Name=$name, Phone=$phone, Email=$email")

            } catch (e: Exception) {
                Log.e("SignUpActivity", "Error parsing QR code data", e)
                Toast.makeText(this, "Invalid QR Code Format", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "No QR Data Received", Toast.LENGTH_LONG).show()
        }

        // Submit Button Click Listener
        btnSubmit.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_LONG).show()
            }
        }
    }
}