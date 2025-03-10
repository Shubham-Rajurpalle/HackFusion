package com.cricketapp.hackfusion.application

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.Application.LeaveApplication
import com.cricketapp.hackfusion.Application.LeaveApplicationFacultyAdapter
import com.cricketapp.hackfusion.R
import com.google.firebase.firestore.FirebaseFirestore

class applicationFacultyFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LeaveApplicationFacultyAdapter
    private val leaveApplicationsList = mutableListOf<LeaveApplication>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_application_faculty, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewApplications)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

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
                    leaveApplication.id = document.id // ✅ Store Firestore document ID
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
            .document(application.id) // ✅ Now this is always valid
            .update("status", newStatus)
            .addOnSuccessListener {
                application.status = newStatus
                adapter.updateItem(application) // ✅ Update only modified item in RecyclerView
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating status: ${e.message}")
            }
    }

}
