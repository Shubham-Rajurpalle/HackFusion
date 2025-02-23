package com.cricketapp.hackfusion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cricketapp.hackfusion.databinding.FragmentComplaintFacultyBinding
import com.google.firebase.database.*

class complaintFacultyFragment : Fragment() {
    private var _binding: FragmentComplaintFacultyBinding? = null
    private val binding get() = _binding!!

    private lateinit var complaintAdapter: ComplaintAdapter
    private lateinit var databaseRef: DatabaseReference
    private val complaintList = mutableListOf<Complaint>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComplaintFacultyBinding.inflate(inflater, container, false)
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
            isDean = false
        )

        binding.recyclerViewComplaints.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = complaintAdapter
        }
    }

    private fun fetchComplaints() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerViewComplaints.visibility = View.GONE

        databaseRef = FirebaseDatabase.getInstance().reference.child("Complaints")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                complaintList.clear()
                for (complaintSnapshot in snapshot.children) {
                    val complaint = complaintSnapshot.getValue(Complaint::class.java)
                    complaint?.let {
                        complaintList.add(it)
                    }
                }
                complaintAdapter.updateList(complaintList)

                binding.progressBar.visibility = View.GONE
                binding.recyclerViewComplaints.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    context,
                    "Failed to load complaints: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}