package com.cricketapp.hackfusion.Application

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.R
import com.google.firebase.firestore.FirebaseFirestore

class applicationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LeaveApplicationAdapter
    private val leaveApplicationsList = mutableListOf<LeaveApplication>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_application, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val applyLeave= view.findViewById<Button>(R.id.btnApplyLeave)
        applyLeave.setOnClickListener {
            val intent = Intent(requireContext(), ApplyLeaveActivity::class.java)
            startActivity(intent)
        }

        val applyApplication= view.findViewById<Button>(R.id.btnApplyApplication)
        applyApplication.setOnClickListener {
            val intent = Intent(requireContext(), ApplyApprovalActivity::class.java)
            startActivity(intent)
        }

        recyclerView = view.findViewById(R.id.recyclerViewApplications)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

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
