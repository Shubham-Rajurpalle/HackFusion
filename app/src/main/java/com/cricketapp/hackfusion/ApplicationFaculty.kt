package com.cricketapp.hackfusion.application

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.Application.LeaveApplication
import com.cricketapp.hackfusion.Application.LeaveApplicationFacultyAdapter
import com.cricketapp.hackfusion.R
import com.google.firebase.firestore.FirebaseFirestore

class ApplicationFaculty : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LeaveApplicationFacultyAdapter
    private val leaveApplicationsList = mutableListOf<LeaveApplication>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_faculty)

        recyclerView = findViewById(R.id.recyclerViewApplications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = LeaveApplicationFacultyAdapter(leaveApplicationsList) { application, isApproved ->
            updateApplicationStatus(application, isApproved)
        }
        recyclerView.adapter = adapter

        fetchLeaveApplications()
    }

    private fun fetchLeaveApplications() {
        FirebaseFirestore.getInstance().collection("leave_applications")
            .get()
            .addOnSuccessListener { result ->
                leaveApplicationsList.clear()
                for (document in result) {
                    val leaveApplication = document.toObject(LeaveApplication::class.java)
                    leaveApplication.id = document.id // Store Firestore document ID
                    leaveApplicationsList.add(leaveApplication)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching data: ${exception.message}")
            }
    }

    private fun updateApplicationStatus(application: LeaveApplication, isApproved: Boolean) {
        if (application.id.isEmpty()) {
            Log.e("Firestore", "Cannot update: Application ID is empty")
            return
        }

        val newStatus = if (isApproved) "Approved" else "Rejected"

        FirebaseFirestore.getInstance().collection("leave_applications")
            .document(application.id) // Now this is always valid
            .update("status", newStatus)
            .addOnSuccessListener {
                application.status = newStatus
                adapter.updateItem(application) // Update only modified item in RecyclerView
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating status: ${e.message}")
            }
    }
}