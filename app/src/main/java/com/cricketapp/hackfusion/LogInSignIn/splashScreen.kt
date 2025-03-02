package com.cricketapp.hackfusion.LogInSignIn

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cricketapp.hackfusion.Home.DeanHomeActivity
import com.cricketapp.hackfusion.Home.DoctorHomeActivity
import com.cricketapp.hackfusion.Home.FacultyHomeActivity
import com.cricketapp.hackfusion.Home.SecurityHomeActivity
import com.cricketapp.hackfusion.Home.home
import com.cricketapp.hackfusion.R

class splashScreen : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)

        Handler(Looper.getMainLooper()).postDelayed({
            navigateBasedOnRole()
        }, 2000)
    }

    private fun navigateBasedOnRole() {
        val isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false)
        val userRole = sharedPreferences.getString("USER_ROLE", "")

        val nextScreen = if (isLoggedIn) {
            when (userRole) {
                "Student" -> home::class.java
                "Security" -> SecurityHomeActivity::class.java
                "Doctor" -> DoctorHomeActivity::class.java
                "Faculty" -> FacultyHomeActivity::class.java
                "Dean" -> DeanHomeActivity::class.java
                else -> {
                    startActivity(Intent(this, signIn::class.java))
                    finish()
                    return
                }
            }
        } else {
            signIn::class.java
        }

        startActivity(Intent(this, nextScreen))
        finish()
    }
}
