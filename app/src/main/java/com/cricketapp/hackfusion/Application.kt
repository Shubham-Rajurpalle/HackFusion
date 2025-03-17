// File: ApplicationActivity.kt
package com.cricketapp.hackfusion.Application

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.R
import com.google.firebase.firestore.FirebaseFirestore

class Application : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LeaveApplicationAdapter
    private val leaveApplicationsList = mutableListOf<LeaveApplication>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application)

        val applyLeave = findViewById<Button>(R.id.btnApplyLeave)
        applyLeave.setOnClickListener {
            val intent = Intent(this, ApplyLeaveActivity::class.java)
            startActivity(intent)
        }

        val applyApplication = findViewById<Button>(R.id.btnApplyApplication)
        applyApplication.setOnClickListener {
            val intent = Intent(this, ApplyApprovalActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerViewApplications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = LeaveApplicationAdapter(leaveApplicationsList) { application, isApproved ->
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
                    leaveApplicationsList.add(leaveApplication)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching data: ${exception.message}")
            }
    }

    private fun updateApplicationStatus(application: LeaveApplication, isApproved: Boolean) {
        val newStatus = if (isApproved) "Approved" else "Rejected"

        FirebaseFirestore.getInstance().collection("leave_applications")
            .document(application.id)
            .update("status", newStatus)
            .addOnSuccessListener {
                application.status = newStatus
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating status: ${e.message}")
            }
    }
}