package com.cricketapp.hackfusion

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CreateElectionActivity : AppCompatActivity() {

    private lateinit var etElectionName: EditText
    private lateinit var etCandidates: EditText
    private lateinit var btnPickStartTime: Button
    private lateinit var btnPickEndTime: Button
    private lateinit var tvStartTime: TextView
    private lateinit var tvEndTime: TextView
    private lateinit var btnCreateElection: Button

    private lateinit var db: FirebaseFirestore
    private var startTime: Long = 0
    private var endTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_election)

        db = FirebaseFirestore.getInstance()

        // Initialize Views
        etElectionName = findViewById(R.id.etElectionName)
        etCandidates = findViewById(R.id.etCandidates)
        btnPickStartTime = findViewById(R.id.btnPickStartTime)
        btnPickEndTime = findViewById(R.id.btnPickEndTime)
        tvStartTime = findViewById(R.id.tvStartTime)
        tvEndTime = findViewById(R.id.tvEndTime)
        btnCreateElection = findViewById(R.id.btnCreateElection)

        // Date & Time Picker for Start Time
        btnPickStartTime.setOnClickListener { showDateTimePicker { time ->
            startTime = time
            tvStartTime.text = "Start Time: " + formatTime(time)
        } }

        // Date & Time Picker for End Time
        btnPickEndTime.setOnClickListener { showDateTimePicker { time ->
            endTime = time
            tvEndTime.text = "End Time: " + formatTime(time)
        } }

        // Create Election
        btnCreateElection.setOnClickListener { createElection() }
    }

    private fun createElection() {
        val electionName = etElectionName.text.toString().trim()
        val candidatesText = etCandidates.text.toString().trim()

        // Validation
        if (electionName.isEmpty()) {
            Toast.makeText(this, "Please enter an election name", Toast.LENGTH_SHORT).show()
            return
        }
        if (candidatesText.isEmpty()) {
            Toast.makeText(this, "Please enter at least one candidate", Toast.LENGTH_SHORT).show()
            return
        }
        if (startTime == 0L || endTime == 0L || endTime <= startTime) {
            Toast.makeText(this, "Please select valid start and end times", Toast.LENGTH_SHORT).show()
            return
        }

        val candidates = candidatesText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

        // Prepare Election Data
        val election = Election(
            id = UUID.randomUUID().toString(),
            electionName = electionName,
            candidates = candidates,
            votes = candidates.associateWith { 0 }, // Initialize votes to 0 for each candidate
            voters = mutableMapOf(),
            startTime = startTime,
            endTime = endTime,
            totalVotes = 0,
            status = true,  // Election is active when created
            winner = null
        )

        // Store in Firestore
        db.collection("elections").document(election.id)
            .set(election)
            .addOnSuccessListener {
                Toast.makeText(this, "Election Created Successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to show Date and Time Picker
    private fun showDateTimePicker(callback: (Long) -> Unit) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(this, { _, year, month, day ->
            TimePickerDialog(this, { _, hour, minute ->
                calendar.set(year, month, day, hour, minute)
                callback(calendar.timeInMillis)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    // Format Timestamp to Readable Date-Time
    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}