package com.cricketapp.hackfusion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SecurityHomeActivity : AppCompatActivity() {

    private lateinit var btnNotify: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_home)

        // Initialize button
        btnNotify = findViewById(R.id.scan)

        // Set click listener
        btnNotify.setOnClickListener {
            startActivity(Intent(this, SecurityQrScan::class.java))
        }
    }
}
