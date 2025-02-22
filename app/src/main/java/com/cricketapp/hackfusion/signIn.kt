package com.cricketapp.hackfusion

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class signIn : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var btnSignUp: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)
        btnSignUp = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, qrScanner::class.java))
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (!email.endsWith("@sggs.ac.in")) {
                etEmail.error = "Use a valid @sggs.ac.in email"
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPassword.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            updateEmailVerified(user.uid)
                            saveLoginState(user.uid)
                            checkUserRole(user.uid)
                        }
                    } else {
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun checkUserRole(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")

                    Log.d("SIGN_IN", "Fetched Role: $role")  // Debugging log
                    Log.d("SIGN_IN", "Firestore Data: ${document.data}")  // Log all data

                    if (role != null) {
                        saveUserRole(role)  // ✅ Store in SharedPreferences

                        val intent = when (role) {
                            "Student" -> Intent(this, home::class.java)
                            "Security" -> Intent(this, SecurityHomeActivity::class.java)
                            "Doctor" -> Intent(this, DoctorHomeActivity::class.java)
                            "Faculty" -> Intent(this, FacultyHomeActivity::class.java)
                            "Dean" -> Intent(this, DeanHomeActivity::class.java)
                            else -> {
                                Toast.makeText(this, "Unknown role, contact admin.", Toast.LENGTH_LONG).show()
                                null
                            }
                        }
                        intent?.let {
                            startActivity(it)
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "Role not found in Firestore!", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "User data missing in Firestore!", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("SIGN_IN", "Firestore Error: ${e.message}")
                Toast.makeText(this, "Failed to fetch user role.", Toast.LENGTH_LONG).show()
            }
    }

    private fun updateEmailVerified(userId: String) {
        db.collection("users").document(userId).update("isEmailVerified", true)
            .addOnFailureListener { e ->
                Log.e("SIGN_IN", "Failed to update email verification: ${e.message}")
            }
    }

    private fun saveLoginState(userId: String) {
        val editor = sharedPreferences.edit()
        editor.putString("USER_ID", userId)
        editor.putBoolean("IS_LOGGED_IN", true)
        editor.apply()
    }

    private fun saveUserRole(role: String) {
        val editor = sharedPreferences.edit()
        editor.putString("USER_ROLE", role)
        editor.apply()
    }

}
