package com.cricketapp.hackfusion.Home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.cricketapp.hackfusion.R
import com.cricketapp.hackfusion.QrScanDoctorSecurity.SecurityQrScan
import com.cricketapp.hackfusion.profile_activity

class SecurityHomeActivity : AppCompatActivity() {

    private lateinit var btnNotify: Button
    private lateinit var profilePhoto:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_home)

        // Initialize button
        btnNotify = findViewById(R.id.scan)
        profilePhoto=findViewById(R.id.profileBtn)

        // Set click listener
        btnNotify.setOnClickListener {
            startActivity(Intent(this, SecurityQrScan::class.java))
        }

        profilePhoto.setOnClickListener {
            startActivity(Intent(this, profile_activity::class.java))
        }
    }
}
