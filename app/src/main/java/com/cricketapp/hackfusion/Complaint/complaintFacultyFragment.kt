package com.cricketapp.hackfusion.Complaint

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cricketapp.hackfusion.databinding.FragmentComplaintFacultyBinding
import com.google.firebase.database.*


class ComplaintFacultyFragment : Fragment() {
    private var _binding: FragmentComplaintFacultyBinding? = null
    private val binding get() = _binding!!

    private lateinit var complaintAdapter: ComplaintApprovalAdapter
    private val complaintList = ArrayList<Complaint>()
    private lateinit var databaseRef: DatabaseReference
    private var isDean: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComplaintFacultyBinding.inflate(inflater, container, false)

        databaseRef = FirebaseDatabase.getInstance().reference.child("Complaints")

        setupRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fetch data after view is created
        fetchComplaintsFromFirebase()
    }

    private fun setupRecyclerView() {
        Log.d("ComplaintFragment", "Setting up RecyclerView")

        binding.recyclerViewComplaints.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)

            // Initialize adapter with empty list
            complaintAdapter = ComplaintApprovalAdapter(
                complaints = complaintList,
                isDean = isDean
            ) { complaint, isApproved ->
                updateComplaintStatus(complaint, isApproved)
            }

            adapter = complaintAdapter

            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        Log.d("ComplaintFragment", "RecyclerView setup completed")
    }

    private fun fetchComplaintsFromFirebase() {
        Log.d("ComplaintFragment", "Starting to fetch complaints")

        if (!::databaseRef.isInitialized) {
            Log.e("ComplaintFragment", "Database reference not initialized")
            return
        }

        Log.d("ComplaintFragment", "Database path: ${databaseRef.path}")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d("ComplaintFragment", "Data snapshot received: ${snapshot.exists()}")
                    Log.d("ComplaintFragment", "Children count: ${snapshot.childrenCount}")

                    complaintList.clear()

                    for (complaintSnapshot in snapshot.children) {
                        Log.d("ComplaintFragment", "Processing complaint: ${complaintSnapshot.key}")

                        try {
                            val complaint = complaintSnapshot.getValue(Complaint::class.java)
                            Log.d("ComplaintFragment", "Complaint data: $complaint")

                            complaint?.let {
                                it.id = complaintSnapshot.key ?: return@let
                                complaintList.add(it)
                                Log.d("ComplaintFragment", "Added complaint: ${it.id}")
                            }
                        } catch (e: Exception) {
                            Log.e("ComplaintFragment", "Error parsing complaint", e)
                            // Print the raw data for debugging
                            Log.d("ComplaintFragment", "Raw data: ${complaintSnapshot.value}")
                        }
                    }

                    Log.d("ComplaintFragment", "Final complaints list size: ${complaintList.size}")

                    // Update UI on main thread
                    activity?.runOnUiThread {
                        complaintAdapter.updateComplaints(complaintList)

                        // Update visibility of RecyclerView and empty state
                        binding.recyclerViewComplaints.visibility =
                            if (complaintList.isEmpty()) View.GONE else View.VISIBLE
                        binding.tvHeaderComplaint?.visibility =
                            if (complaintList.isEmpty()) View.VISIBLE else View.GONE
                    }

                } catch (e: Exception) {
                    Log.e("ComplaintFragment", "Error processing complaints", e)
                    showToast("Error loading complaints: ${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ComplaintFragment", "Database error: ${error.message}", error.toException())
                showToast("Failed to load complaints: ${error.message}")
            }
        })
    }


    private fun updateComplaintStatus(complaint: Complaint, isApproved: Boolean) {
        val complaintRef = databaseRef.child(complaint.id)

        complaintRef.updateChildren(mapOf("approved" to isApproved))
            .addOnSuccessListener {
                val status = if (isApproved) "resolved" else "rejected"
                showToast("Complaint $status successfully")
            }
            .addOnFailureListener { e ->
                Log.e("ComplaintFragment", "Error updating complaint", e)
                showToast("Update failed: ${e.message}")
            }
    }

    private fun showToast(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}