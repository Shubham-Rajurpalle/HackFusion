package com.cricketapp.hackfusion

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the user is already logged in
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            navigateToHome()
            return
        }

        setContentView(R.layout.activity_sign_up)

        // Initialize Views
        etRole = findViewById(R.id.etRole)
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        etParentEmail = findViewById(R.id.etParentEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Get QR Data from Intent
        val qrData = intent.getStringExtra("QR_CODE_DATA")

        if (!qrData.isNullOrEmpty()) {
            try {
                // ✅ Convert QR string to JSON
                val jsonData = JSONObject(qrData)

                // ✅ Extract fields safely
                val role = jsonData.optString("role", "").takeIf { it != "null" } ?: ""
                val name = jsonData.optString("name", "").takeIf { it != "null" } ?: ""
                val phone = jsonData.optString("self_phone", "").takeIf { it != "null" } ?: ""
                val email = jsonData.optString("self_mail", "").takeIf { it != "null" } ?: ""
                val parentEmail = jsonData.optString("parent_mail", "").takeIf { it != "null" } ?: ""

                // ✅ Pre-fill EditText fields
                etRole.setText(role)
                etName.setText(name)
                etPhone.setText(phone)
                etEmail.setText(email)
                etParentEmail.setText(parentEmail)

                Log.d("SignUpActivity", "Auto-filled: Role=$role, Name=$name, Phone=$phone, Email=$email, ParentEmail=$parentEmail")

            } catch (e: Exception) {
                Log.e("SignUpActivity", "Error parsing QR code data", e)
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

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || role.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, phone) // Using phone as dummy password
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                    val user = hashMapOf("userId" to userId, "name" to name, "email" to email, "phone" to phone, "role" to role)

                    db.collection("users").document(userId)
                        .set(user)
                        .addOnSuccessListener {
                            // ✅ Save User Data in SharedPreferences
                            val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("name", name)
                            editor.putString("email", email)
                            editor.putString("phone", phone)
                            editor.putString("role", role)
                            editor.apply()

                            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, home::class.java))
                            finish()
                        }
                }
            }
    }

    private fun navigateToHome() {
        val intent = Intent(this, home::class.java)
        startActivity(intent)
        finish()
    }
}