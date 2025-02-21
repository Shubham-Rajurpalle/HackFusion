package com.cricketapp.hackfusion

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
    private lateinit var adapter: ElectionAdapter
    private val electionList = mutableListOf<Election>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        db = FirebaseFirestore.getInstance()
        binding.electionRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ElectionAdapter(emptyList()) { election -> openElectionFragment(election.id) }
        binding.electionRecyclerView.adapter = adapter

        fetchElections()
        return view
    }

    private fun fetchElections() {
        db.collection("elections")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    println("No Elections Found")
                    return@addOnSuccessListener
                }

                electionList.clear()
                for (doc in snapshot.documents) {
                    val election = doc.toObject(Election::class.java)?.copy(id = doc.id)
                    election?.let { electionList.add(it) }
                }

                adapter.updateList(electionList)
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
