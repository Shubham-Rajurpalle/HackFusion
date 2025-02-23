package com.cricketapp.hackfusion

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.models.LeaveApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class budgetFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LeaveApplicationAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find buttons
        val btnApplyLeave = view.findViewById<Button>(R.id.btnApplyLeave)
        val btnApplyApproval = view.findViewById<Button>(R.id.btnApplyApproval)

        // Set click listeners for buttons
        btnApplyLeave?.setOnClickListener {
            val intent = Intent(requireContext(), ApplyLeaveActivity::class.java)
            startActivity(intent)
        }

        btnApplyApproval?.setOnClickListener {
            val intent = Intent(requireContext(), ApplyApprovalActivity::class.java)
            startActivity(intent)
        }

        recyclerView = view.findViewById(R.id.recyclerViewApplications)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = LeaveApplicationAdapter(emptyList()) // Corrected Adapter Initialization
        recyclerView.adapter = adapter

        fetchLeaveApplications()
    }

    private fun fetchLeaveApplications() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = currentUser?.uid // Get current user's ID

        if (currentUserId != null) {
            db.collection("leave_applications")
                .whereEqualTo("userId", currentUserId) // Filter by userId
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener { documents ->
                    val leaveList = mutableListOf<LeaveApplication>()
                    for (document in documents) {
                        val leaveApp = document.toObject(LeaveApplication::class.java)
                        leaveList.add(leaveApp)
                    }
                    adapter.updateData(leaveList)
                }
                .addOnFailureListener { e ->
                    println("Error fetching leave applications: ${e.message}")
                }
        }
    }

}
