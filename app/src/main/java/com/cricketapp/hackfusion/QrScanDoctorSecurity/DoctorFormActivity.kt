package com.cricketapp.hackfusion.QrScanDoctorSecurity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cricketapp.hackfusion.Home.DoctorHomeActivity
import com.cricketapp.hackfusion.JavaMailAPI
import com.cricketapp.hackfusion.R
import org.json.JSONObject

class DoctorFormActivity : AppCompatActivity() {

    private lateinit var etRegNo: EditText
    private lateinit var etName: EditText
    private lateinit var etParentEmail: EditText
    private lateinit var btnSubmit: Button
    private var parentEmail: String = "" // Store email to use in Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_form)

        etName = findViewById(R.id.etName)
        etRegNo = findViewById(R.id.regno)
        etParentEmail = findViewById(R.id.email)
        btnSubmit = findViewById(R.id.btnSubmit)

        val qrData = intent.getStringExtra("QR_CODE_DATA") ?: "{}"
        parseQRCodeData(qrData)

        btnSubmit.setOnClickListener {
            sendEmailToParent()
        }
    }

    private fun parseQRCodeData(qrData: String) {
        try {
            val jsonData = JSONObject(qrData)
            etRegNo.setText(jsonData.optString("reg_no", "N/A"))
            etName.setText(jsonData.optString("name", "N/A"))
            parentEmail = jsonData.optString("parent_mail", "")
            etParentEmail.setText(parentEmail)

            println("QR Parsed Data: regNo=${etRegNo.text}, name=${etName.text}, email=$parentEmail")

        } catch (e: Exception) {
            Toast.makeText(this, "Invalid QR Code Format", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendEmailToParent() {
        if (parentEmail.isNotEmpty()) {
            val subject = "Student Health Checkup Update"
            val message = """
            Dear Parent,
            
            This is to inform you that your child, ${etName.text}, (Reg No: ${etRegNo.text}) has been checked by the doctor.
            
            Please feel free to contact us if you need further details.
            
            Regards,
            College Medical Team
        """.trimIndent()

            JavaMailAPI(parentEmail, subject, message) { success ->
                if (success) {
                    Toast.makeText(this, "Email sent successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send email!", Toast.LENGTH_SHORT).show()
                }

                // Return to DoctorHomeActivity
                navigateBackToHome()
            }.execute()
        } else {
            Toast.makeText(this, "Parent email is missing!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun navigateBackToHome() {
        val intent = Intent(this, DoctorHomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()  // Close current activity
    }
}