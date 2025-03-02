package com.cricketapp.hackfusion.Notification

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cricketapp.hackfusion.R
import com.google.firebase.firestore.FirebaseFirestore

class CreateNotificationActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var createBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_notification)

        titleInput = findViewById(R.id.titleInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        createBtn = findViewById(R.id.createNotificationButton)

        createBtn.setOnClickListener {
            saveNotification()
        }
    }

    private fun saveNotification() {
        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            return
        }

        val notification = Notification(
            title = title,
            description = description,
            imageUrl = "",  // No image URL, so just store an empty string
            timestamp = System.currentTimeMillis()
        )

        FirebaseFirestore.getInstance().collection("activities")
            .add(notification)
            .addOnSuccessListener {
                Toast.makeText(this, "Notification Created", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to create notification", Toast.LENGTH_SHORT).show()
            }
    }
}