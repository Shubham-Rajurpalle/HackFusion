package com.cricketapp.hackfusion

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class Security_Qr_Code_Layout : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_security_qr_code_layout)

        val etName = findViewById<EditText>(R.id.etName)
        val etRegNo = findViewById<EditText>(R.id.etRegNo)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        val qrData = intent.getStringExtra("qr_data") ?: "{}"
        var childName = ""

        if (qrData.isNotEmpty()) {
            try {
                val jsonData = JSONObject(qrData)
                childName = jsonData.optString("name", "")
                etName.setText(childName)
                etEmail.setText(jsonData.optString("parent_mail", ""))
                etRegNo.setText(jsonData.optString("reg_no", ""))
            } catch (e: Exception) {
                Log.e("SignUpActivity", "Error parsing QR data", e)
                Toast.makeText(this, "Invalid QR Code Format", Toast.LENGTH_LONG).show()
            }
        }

        // Set up the Submit button click listener
        btnSubmit.setOnClickListener {
            val parentMail = etEmail.text.toString()
            if (parentMail.isNotEmpty()) {
                // Prepare email details with the child's name included
                val subject = "$childName has left the campus"
                val body = "Dear Parent,\n\nYour child, $childName, has left the campus. Please make sure to verify their whereabouts.\n\nRegards,\nSecurity Team"
                sendEmail(parentMail, subject, body)
            } else {
                Toast.makeText(this, "Parent email is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to send email using JavaMailAPI
    private fun sendEmail(toEmail: String, subject: String, body: String) {
        // Assuming JavaMailAPI setup has already been done
        val mailAPI = JavaMailAPI(
            toEmail,
            subject,
            body,
            object : ((Boolean) -> Unit) {
                override fun invoke(success: Boolean) {
                    if (success) {
                        Toast.makeText(this@Security_Qr_Code_Layout, "Email sent successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@Security_Qr_Code_Layout, "Failed to send email", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        // Sending the email
        mailAPI.execute()
    }
}
