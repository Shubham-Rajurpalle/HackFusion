package com.cricketapp.hackfusion.Home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cricketapp.hackfusion.Notification.NotificationAdapter
import com.cricketapp.hackfusion.ConductVoilate.CaughtStudentAdapter
import com.cricketapp.hackfusion.Election.CreateElectionActivity
import com.cricketapp.hackfusion.Notification.CreateNotificationActivity
import com.cricketapp.hackfusion.Election.Election
import com.cricketapp.hackfusion.Election.ElectionAdapter
import com.cricketapp.hackfusion.Election.ElectionFragment
import com.cricketapp.hackfusion.Notification.Notification
import com.cricketapp.hackfusion.R
import com.cricketapp.hackfusion.Student
import com.cricketapp.hackfusion.databinding.FragmentDeanHomeBinding
import com.cricketapp.hackfusion.profile_activity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class DeanHomeFragment : Fragment() {

    private var _binding: FragmentDeanHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore
    private lateinit var electionAdapter: ElectionAdapter
    private val electionList = mutableListOf<Election>()

    private lateinit var notificationAdapter: NotificationAdapter
    private val notificationList = mutableListOf<Notification>()

    private lateinit var caughtStudentsAdapter: CaughtStudentAdapter
    private val caughtStudentsList = mutableListOf<Student>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeanHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        setupRecyclerViews()
        fetchData()

        binding.profileBtn.setOnClickListener {
            startActivity(Intent(requireContext(), profile_activity::class.java))
        }

        binding.createElectionBtn.setOnClickListener {
            startActivity(Intent(requireContext(), CreateElectionActivity::class.java))
        }

        binding.createNotification.setOnClickListener {
            startActivity(Intent(requireContext(), CreateNotificationActivity::class.java))
        }

        fetchUserName() // ✅ Calling AFTER View is created
    }

    private fun fetchUserName() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (!isAdded || _binding == null) return@addOnSuccessListener // ✅ Avoid NPE

                val userName = document.getString("name") ?: "User"
                binding.toolbar?.findViewById<TextView>(R.id.tvUserName)?.text = "Hello $userName"
            }
            .addOnFailureListener { e ->
                println("Error fetching user: ${e.message}")
            }
    }

    private fun setupRecyclerViews() {
        binding.electionRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        electionAdapter = ElectionAdapter(mutableListOf()) { election -> openElectionFragment(election.id) }
        binding.electionRecyclerView.adapter = electionAdapter

        binding.activityRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        notificationAdapter = NotificationAdapter(mutableListOf())
        binding.activityRecyclerView.adapter = notificationAdapter

        binding.caughtStudentRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        caughtStudentsAdapter = CaughtStudentAdapter(mutableListOf())
        binding.caughtStudentRecycler.adapter = caughtStudentsAdapter
    }

    private fun fetchData() {
        fetchElections()
        fetchActivities()
        fetchCaughtStudents()
    }

    private fun fetchElections() {
        db.collection("elections")
            .get()
            .addOnSuccessListener { snapshot ->
                if (!isAdded || _binding == null || snapshot.isEmpty) return@addOnSuccessListener

                electionList.clear()
                for (doc in snapshot.documents) {
                    val election = doc.toObject(Election::class.java)?.copy(id = doc.id)
                    election?.let { electionList.add(it) }
                }
                electionAdapter.updateList(electionList)
            }
    }

    private fun fetchActivities() {
        db.collection("activities")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!isAdded || _binding == null || snapshot.isEmpty) return@addOnSuccessListener

                notificationList.clear()
                for (doc in snapshot.documents) {
                    val notification = doc.toObject(Notification::class.java)?.copy(id = doc.id)
                    notification?.let { notificationList.add(it) }
                }
                notificationAdapter.updateList(notificationList)
            }
    }

    private fun fetchCaughtStudents() {
        db.collection("studentsCaught")
            .get()
            .addOnSuccessListener { snapshot ->
                if (!isAdded || _binding == null || snapshot.isEmpty) return@addOnSuccessListener

                caughtStudentsList.clear()
                for (doc in snapshot.documents) {
                    val student = doc.toObject(Student::class.java)?.copy(id = doc.id)
                    student?.let { caughtStudentsList.add(it) }
                }
                caughtStudentsAdapter.updateList(caughtStudentsList)
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
        _binding = null // ✅ Prevent Memory Leaks
    }
}
