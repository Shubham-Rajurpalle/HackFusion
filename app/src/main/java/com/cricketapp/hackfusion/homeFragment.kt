package com.cricketapp.hackfusion

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.cricketapp.hackfusion.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class homeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore
    private lateinit var electionAdapter: ElectionAdapter
    private val electionList = mutableListOf<Election>()

    private lateinit var activityAdapter: ActivityAdapter
    private val notificationList = mutableListOf<Notification>()

    private lateinit var caughtStudentsAdapter: CaughtStudentAdapter
    private val caughtStudentsList = mutableListOf<Student>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        db = FirebaseFirestore.getInstance()

        // Election RecyclerView setup
        binding.electionRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        electionAdapter = ElectionAdapter(mutableListOf()) { election -> openElectionFragment(election.id) }
        binding.electionRecyclerView.adapter = electionAdapter
        fetchElections()

        // Activity RecyclerView setup
        binding.activityRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        activityAdapter = ActivityAdapter(mutableListOf())
        binding.activityRecyclerView.adapter = activityAdapter
        fetchActivities()

        // Caught Students RecyclerView setup
        binding.caughtStudentRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        caughtStudentsAdapter = CaughtStudentAdapter(mutableListOf()) // New Adapter for caught students
        binding.caughtStudentRecycler.adapter = caughtStudentsAdapter
        fetchCaughtStudents()

        binding.profileBtn.setOnClickListener {
            startActivity(Intent(requireContext(), profile_activity::class.java))
        }

        return view
    }

    private fun fetchElections() {
        db.collection("elections")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) return@addOnSuccessListener

                electionList.clear()
                for (doc in snapshot.documents) {
                    val election = doc.toObject(Election::class.java)?.copy(id = doc.id)
                    election?.let { electionList.add(it) }
                }
                electionAdapter.updateList(electionList)
            }
            .addOnFailureListener { e ->
                println("Firestore Error: ${e.message}")
            }
    }

    private fun fetchActivities() {
        db.collection("activities")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) return@addOnSuccessListener

                notificationList.clear()
                for (doc in snapshot.documents) {
                    val notification = doc.toObject(Notification::class.java)?.copy(id = doc.id)
                    notification?.let { notificationList.add(it) }
                }
                activityAdapter.updateList(notificationList)
            }
            .addOnFailureListener { e ->
                println("Firestore Error: ${e.message}")
            }
    }

    private fun fetchCaughtStudents() {
        db.collection("studentsCaught")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) return@addOnSuccessListener

                caughtStudentsList.clear()
                for (doc in snapshot.documents) {
                    val student = doc.toObject(Student::class.java)?.copy(id = doc.id)
                    student?.let { caughtStudentsList.add(it) }
                }
                caughtStudentsAdapter.updateList(caughtStudentsList)
            }
            .addOnFailureListener { e ->
                println("Firestore Error: ${e.message}")
            }
    }

    private fun openElectionFragment(electionId: String) {
        val electionFragment = ElectionFragment().apply {
            arguments = Bundle().apply { putString("electionId", electionId) }
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.navHost, electionFragment)
            .addToBackStack(null)
            .commit()

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.electionIcon
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
