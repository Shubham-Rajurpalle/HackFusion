package com.cricketapp.hackfusion

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject

class Security_Qr_Code_Layout : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_security_qr_code_layout)



        val etName = findViewById<EditText>(R.id.etName)
        val etRegNo = findViewById<EditText>(R.id.etRegNo)
        val etEmail = findViewById<EditText>(R.id.etEmail)

        val qrData = intent.getStringExtra("qr_data") ?: "{}"
        if (qrData.isNotEmpty()) {
            try {
                val jsonData = JSONObject(qrData)
                etName.setText(jsonData.optString("name", ""))
                etEmail.setText(jsonData.optString("parent_mail", ""))
                etRegNo.setText((jsonData.optString("reg_no","")))
            } catch (e: Exception) {
                Log.e("SignUpActivity", "Error parsing QR data", e)
                Toast.makeText(this, "Invalid QR Code Format", Toast.LENGTH_LONG).show()
            }
        }

    }
}