//package com.cricketapp.hackfusion
//
//import android.content.Intent
//import android.content.SharedPreferences
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ProgressBar
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import org.json.JSONObject
//
//class signUpSignInEmailVerification {
//
//    package com.cricketapp.hackfusion
//
//    import android.content.Intent
//    import android.content.SharedPreferences
//    import android.os.Bundle
//    import android.util.Log
//    import android.widget.Button
//    import android.widget.EditText
//    import android.widget.Toast
//    import androidx.appcompat.app.AppCompatActivity
//    import com.google.firebase.auth.FirebaseAuth
//    import com.google.firebase.firestore.FirebaseFirestore
//    import org.json.JSONObject
//
//    class signUp : AppCompatActivity() {
//
//        private lateinit var etRole: EditText
//        private lateinit var etName: EditText
//        private lateinit var etPhone: EditText
//        private lateinit var etEmail: EditText
//        private lateinit var etParentEmail: EditText
//        private lateinit var etPassword: EditText
//        private lateinit var btnSubmit: Button
//
//        private lateinit var auth: FirebaseAuth
//        private lateinit var db: FirebaseFirestore
//        private lateinit var sharedPreferences: SharedPreferences
//
//        override fun onCreate(savedInstanceState: Bundle?) {
//            super.onCreate(savedInstanceState)
//
//            // Check if the user is already logged in
//            sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
//            if (sharedPreferences.getBoolean("isLoggedIn", false)) {
//                navigateToHome()
//                return
//            }
//
//            setContentView(R.layout.activity_sign_up)
//
//            // Initialize Views
//            etRole = findViewById(R.id.etRole)
//            etName = findViewById(R.id.etName)
//            etPhone = findViewById(R.id.etPhone)
//            etEmail = findViewById(R.id.etEmail)
//            etParentEmail = findViewById(R.id.etParentEmail)
//            etPassword = findViewById(R.id.etPassword)
//            btnSubmit = findViewById(R.id.btnSubmit)
//
//            // Initialize Firebase
//            auth = FirebaseAuth.getInstance()
//            db = FirebaseFirestore.getInstance()
//
//            // Get QR Data from Intent
//            val qrData = intent.getStringExtra("QR_CODE_DATA")
//
//            if (!qrData.isNullOrEmpty()) {
//                try {
//                    // ✅ Convert QR string to JSON
//                    val jsonData = JSONObject(qrData)
//
//                    // ✅ Extract fields safely
//                    val role = jsonData.optString("role", "").takeIf { it != "null" } ?: ""
//                    val name = jsonData.optString("name", "").takeIf { it != "null" } ?: ""
//                    val phone = jsonData.optString("self_phone", "").takeIf { it != "null" } ?: ""
//                    val email = jsonData.optString("self_mail", "").takeIf { it != "null" } ?: ""
//                    val parentEmail = jsonData.optString("parent_mail", "").takeIf { it != "null" } ?: ""
//
//                    // ✅ Pre-fill EditText fields
//                    etRole.setText(role)
//                    etName.setText(name)
//                    etPhone.setText(phone)
//                    etEmail.setText(email)
//                    etParentEmail.setText(parentEmail)
//
//                    Log.d("SignUpActivity", "Auto-filled: Role=$role, Name=$name, Phone=$phone, Email=$email, ParentEmail=$parentEmail")
//
//                } catch (e: Exception) {
//                    Log.e("SignUpActivity", "Error parsing QR code data", e)
//                    Toast.makeText(this, "Invalid QR Code Format", Toast.LENGTH_LONG).show()
//                }
//            }
//
//            // Submit Button Click Listener
//            btnSubmit.setOnClickListener {
//                registerUser()
//            }
//        }
//
//        private fun registerUser() {
//            val name = etName.text.toString().trim()
//            val email = etEmail.text.toString().trim()
//            val phone = etPhone.text.toString().trim()
//            val role = etRole.text.toString().trim()
//            val password = etPassword.text.toString().trim()
//            val qrData = intent.getStringExtra("QR_CODE_DATA") ?: ""  // Get full QR Data
//
//            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || role.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
//                return
//            }
//
//            auth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val user = auth.currentUser
//                        user?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
//                            if (emailTask.isSuccessful) {
//                                Toast.makeText(this, "Verification email sent. Please verify before logging in.", Toast.LENGTH_LONG).show()
//
//                                // 🔹 Store user data in Firestore following the required format
//                                val userId = user.uid
//                                val userData = hashMapOf(
//                                    "email" to email,
//                                    "role" to role,
//                                    "qrData" to qrData,  // Store full QR Data
//                                    "isEmailVerified" to false // Default false, updated later
//                                )
//
//                                db.collection("users").document(userId).set(userData)
//                                    .addOnSuccessListener {
//                                        Log.d("SignUpActivity", "User data successfully stored in Firestore")
//                                    }
//                                    .addOnFailureListener { e ->
//                                        Log.e("SignUpActivity", "Error saving user data", e)
//                                    }
//
//                                auth.signOut() // Log out user until they verify
//                                startActivity(Intent(this, signIn::class.java))
//                                finish()
//                            } else {
//                                Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    } else {
//                        Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
//                    }
//                }
//        }
//
//
//        private fun navigateToHome() {
//            val intent = Intent(this, home::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }
//
//
//
//
//
//    package com.cricketapp.hackfusion
//    import android.content.Intent
//    import android.os.Bundle
//    import android.view.View
//    import android.widget.*
//    import androidx.appcompat.app.AppCompatActivity
//    import com.google.firebase.auth.FirebaseAuth
//    import com.google.firebase.firestore.FirebaseFirestore
//
//    class signIn : AppCompatActivity() {
//
//        private lateinit var auth: FirebaseAuth
//        private lateinit var db: FirebaseFirestore
//        private lateinit var etEmail: EditText
//        private lateinit var etPassword: EditText
//        private lateinit var btnLogin: Button
//        private lateinit var btnResendVerification: Button
//        private lateinit var progressBar: ProgressBar
//        private lateinit var btnSignUp: Button
//
//        override fun onCreate(savedInstanceState: Bundle?) {
//            super.onCreate(savedInstanceState)
//            setContentView(R.layout.activity_sign_in)
//
//            auth = FirebaseAuth.getInstance()
//            db = FirebaseFirestore.getInstance()
//
//            etEmail = findViewById(R.id.etEmail)
//            etPassword = findViewById(R.id.etPassword)
//            btnLogin = findViewById(R.id.btnLogin)
//            btnResendVerification = findViewById(R.id.btnResendVerification)
//            progressBar = findViewById(R.id.progressBar)
//            btnSignUp=findViewById(R.id.btnSignUp)
//
//            btnSignUp.setOnClickListener {
//                startActivity(Intent(this,qrScanner::class.java))
//            }
//
//            btnLogin.setOnClickListener {
//                val email = etEmail.text.toString().trim()
//                val password = etPassword.text.toString().trim()
//
//                if (!isValidCollegeEmail(email)) {
//                    etEmail.error = "Use a valid @sggs.ac.in email"
//                    return@setOnClickListener
//                }
//
//                if (password.length < 6) {
//                    etPassword.error = "Password must be at least 6 characters"
//                    return@setOnClickListener
//                }
//
//                progressBar.visibility = View.VISIBLE
//
//                auth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener { task ->
//                        progressBar.visibility = View.GONE
//                        if (task.isSuccessful) {
//                            val user = auth.currentUser
//                            if (user != null && user.isEmailVerified) {
//                                updateEmailVerified(user.uid)  // ✅ Update Firestore
//                                checkUserRole(user.uid)  // ✅ Proceed with role check
//                            } else {
//                                Toast.makeText(this, "Email not verified!", Toast.LENGTH_SHORT).show()
//                                btnResendVerification.visibility = View.VISIBLE
//                            }
//                        } else {
//                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
//                        }
//                    }
//            }
//
//            btnResendVerification.setOnClickListener {
//                val user = auth.currentUser
//                user?.sendEmailVerification()?.addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Toast.makeText(this, "Verification email sent!", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(this, "Failed to resend email.", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
//
//        private fun isValidCollegeEmail(email: String): Boolean {
//            return email.endsWith("@sggs.ac.in")
//        }
//
//        private fun checkUserRole(userId: String) {
//            db.collection("users").document(userId).get()
//                .addOnSuccessListener { document ->
//                    if (document.exists()) {
//                        val role = document.getString("role") ?: "Student"
//                        val qrData = document.getString("qrData") ?: "{}"
//
//                        when (role) {
//                            "Student" -> startActivity(Intent(this, home::class.java).putExtra("qrData", qrData))
//                            "Security" -> startActivity(Intent(this, SecurityHomeActivity::class.java).putExtra("qrData", qrData))
//                            "Doctor" -> startActivity(Intent(this, DoctorHomeActivity::class.java).putExtra("qrData", qrData))
//                            "Faculty" -> startActivity(Intent(this, FacultyHomeActivity::class.java).putExtra("qrData", qrData))
//                            "Dean" -> startActivity(Intent(this, DeanHomeActivity::class.java).putExtra("qrData", qrData))
//                            else -> Toast.makeText(this, "Role not recognized!", Toast.LENGTH_SHORT).show()
//                        }
//                        finish()
//                    } else {
//                        Toast.makeText(this, "User role not found!", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                .addOnFailureListener {
//                    Toast.makeText(this, "Failed to get user role", Toast.LENGTH_SHORT).show()
//                }
//        }
//
//        private fun updateEmailVerified(userId: String) {
//            db.collection("users").document(userId)
//                .update("isEmailVerified", true)  // ✅ Update Firestore
//                .addOnSuccessListener {
//                    Toast.makeText(this, "Email Verified!", Toast.LENGTH_SHORT).show()
//                }
//                .addOnFailureListener {
//                    Toast.makeText(this, "Failed to update verification status.", Toast.LENGTH_SHORT).show()
//                }
//        }
//    }
//
//}