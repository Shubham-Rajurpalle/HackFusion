package com.cricketapp.hackfusion

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cricketapp.hackfusion.databinding.FragmentComplaintBinding
import com.google.firebase.database.*

class complaintFragment : Fragment() {

    private lateinit var binding: FragmentComplaintBinding
    private lateinit var database: DatabaseReference
    private lateinit var complaintAdapter: ComplaintAdapter
    private val complaintList = mutableListOf<Complaint>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ✅ Initialize ViewBinding
        binding = FragmentComplaintBinding.inflate(inflater, container, false)
        val view = binding.root

        // ✅ Initialize Firebase Database
        database = FirebaseDatabase.getInstance().getReference("Complaints")

        // ✅ Setup RecyclerView
        binding.recyclerViewComplaints.layoutManager = LinearLayoutManager(requireContext())
        complaintAdapter = ComplaintAdapter(complaintList)
        binding.recyclerViewComplaints.adapter = complaintAdapter

        // ✅ Show loading animation and fetch complaints
        fetchComplaints()

        // ✅ Handle "Submit Complaint" Button Click
        binding.btnSubmitComplaint.setOnClickListener {
            val intent = Intent(requireContext(), complaint_form::class.java)
            startActivity(intent)
        }

        // ✅ Handle "Check Status" Button Click
        binding.btnCheckComplaintStatus.setOnClickListener {
            Toast.makeText(requireContext(), "Fetching latest complaints...", Toast.LENGTH_SHORT).show()
            fetchComplaints() // Refresh data
        }

        return view
    }

    private fun fetchComplaints() {
        // Show ProgressBar while fetching data
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerViewComplaints.visibility = View.GONE

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                complaintList.clear()
                for (complaintSnapshot in snapshot.children) {
                    val complaint = complaintSnapshot.getValue(Complaint::class.java)
                    if (complaint != null) {
                        complaintList.add(complaint)
                    }
                }
                complaintAdapter.updateList(complaintList)

                // Hide ProgressBar and show RecyclerView when data is loaded
                binding.progressBar.visibility = View.GONE
                binding.recyclerViewComplaints.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load complaints!", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.recyclerViewComplaints.visibility = View.VISIBLE
            }
        })
    }
}