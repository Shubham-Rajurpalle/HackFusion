package com.cricketapp.hackfusion.Home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.cricketapp.hackfusion.QrScanDoctorSecurity.QRScanForDoctor
import com.cricketapp.hackfusion.R
import com.cricketapp.hackfusion.profile_activity

class DoctorHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_doctor_home)

        val btnToProfile = findViewById<ImageView>(R.id.profileBtn)
        val btnToQRScanner = findViewById<Button>(R.id.scan)

        btnToProfile.setOnClickListener {
            startActivity(Intent(this, profile_activity::class.java))
        }

        btnToQRScanner.setOnClickListener {
            startActivity(Intent(this, QRScanForDoctor::class.java))
        }
    }
}