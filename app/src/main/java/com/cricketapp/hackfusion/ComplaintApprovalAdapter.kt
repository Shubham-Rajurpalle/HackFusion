package com.cricketapp.hackfusion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.databinding.FragmentComplaintBinding
import com.cricketapp.hackfusion.databinding.FragmentComplaintFacultyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ComplaintApprovalFragment : Fragment() {
    private var _binding: FragmentComplaintBinding? = null
    private val binding get() = _binding!!

    private lateinit var complaintAdapter: ComplaintAdapter
    private val complaintList = ArrayList<Complaint>()
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComplaintBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchComplaints()
    }

    private fun setupRecyclerView() {
        complaintAdapter = ComplaintAdapter(
            complaints = complaintList,
            isDean = true
        ) { complaint, newStatus ->
            updateComplaintStatus(complaint, newStatus)
        }

        binding.recyclerViewComplaints.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = complaintAdapter
        }
    }

    private fun fetchComplaints() {
        databaseRef = FirebaseDatabase.getInstance().reference.child("Complaints")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                complaintList.clear()
                for (complaintSnapshot in snapshot.children) {
                    val complaint = complaintSnapshot.getValue(Complaint::class.java)
                    complaint?.let {
                        it.id = complaintSnapshot.key ?: ""
                        complaintList.add(it)
                    }
                }
                complaintAdapter.updateList(complaintList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load complaints", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateComplaintStatus(complaint: Complaint, newStatus: String) {
        val complaintRef = databaseRef.child(complaint.id)

        complaintRef.child("status").setValue(newStatus)
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Complaint $newStatus successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Update failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
