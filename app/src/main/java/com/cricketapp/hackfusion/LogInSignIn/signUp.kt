package com.cricketapp.hackfusion.LogInSignIn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cricketapp.hackfusion.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

class signUp : AppCompatActivity() {

    private lateinit var etRole: EditText
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etParentEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSubmit: Button
    private lateinit var regno:EditText

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Views
        etRole = findViewById(R.id.etRole)
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        etParentEmail = findViewById(R.id.etParentEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSubmit = findViewById(R.id.btnSubmit)
        regno=findViewById(R.id.regno)


        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Get QR Data from Intent
        val qrData = intent.getStringExtra("QR_CODE_DATA") ?: "{}"
        if (qrData.isNotEmpty()) {
            try {
                val jsonData = JSONObject(qrData)
                etRole.setText(jsonData.optString("role", ""))
                etName.setText(jsonData.optString("name", ""))
                etPhone.setText(jsonData.optString("self_phone", ""))
                etEmail.setText(jsonData.optString("self_mail", ""))
                etParentEmail.setText(jsonData.optString("parent_mail", ""))
                regno.setText((jsonData.optString("reg_no","")))


                Log.d("SignUpActivity", "Auto-filled from QR: $jsonData")
            } catch (e: Exception) {
                Log.e("SignUpActivity", "Error parsing QR data", e)
                Toast.makeText(this, "Invalid QR Code Format", Toast.LENGTH_LONG).show()
            }
        }

        // Submit Button Click Listener
        btnSubmit.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val role = etRole.text.toString().trim()
        val regNo=regno.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val qrData = intent.getStringExtra("QR_CODE_DATA") ?: "{}"

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || role.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                    val userData = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "role" to role,
                        "phone" to phone,
                        "qrData" to qrData,
                        "regNo" to regNo,
                        "isEmailVerified" to false  // Email verification required
                    )

                    db.collection("users").document(userId).set(userData)
                        .addOnSuccessListener {
                            Log.d("SignUpActivity", "User data stored in Firestore")

                            Toast.makeText(this, "Sign-up successful! Please log in.", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, signIn::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.e("SignUpActivity", "Error saving user data", e)
                            Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
