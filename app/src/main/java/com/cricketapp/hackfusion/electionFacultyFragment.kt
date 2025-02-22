package com.cricketapp.hackfusion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cricketapp.hackfusion.databinding.FragmentElectionBinding
import com.google.firebase.firestore.FirebaseFirestore

class electionFacultyFragment : Fragment() {
    private var _binding: FragmentElectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var previousAdapter: PreviousElectionsAdapter
    private lateinit var liveAdapter: LiveElectionFacultyAdapter

    private val previousElectionsList = mutableListOf<Election>()
    private val liveElectionsList = mutableListOf<Election>()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentElectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        fetchPreviousElections()
        fetchLiveElections()
    }

    private fun setupRecyclerViews() {
        // Setup RecyclerView for Previous Elections
        previousAdapter = PreviousElectionsAdapter(previousElectionsList) { election ->
            // Handle previous election selection if needed
        }
        binding.recyclerPreviousElections.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPreviousElections.adapter = previousAdapter

        // Setup RecyclerView for Live Elections
        binding.recyclerLiveElections.layoutManager = LinearLayoutManager(requireContext())
        liveAdapter = LiveElectionFacultyAdapter(liveElectionsList) { election ->
            // Handle view results
            val fragment = result_view()
            val bundle = Bundle()
            bundle.putParcelable("election", election) // Pass election data
            fragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.result_view_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.recyclerLiveElections.adapter = liveAdapter
    }

    private fun fetchPreviousElections() {
        db.collection("elections")
            .whereEqualTo("status", false) // Completed elections
            .get()
            .addOnSuccessListener { documents ->
                previousElectionsList.clear()
                for (document in documents) {
                    val election = document.toObject(Election::class.java)
                    previousElectionsList.add(election)
                }
                previousAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                // Handle error
            }
    }

    private fun fetchLiveElections() {
        db.collection("elections")
            .whereEqualTo("status", true) // Fetch only active elections
            .get()
            .addOnSuccessListener { documents ->
                liveElectionsList.clear()
                for (document in documents) {
                    val election = document.toObject(Election::class.java).copy(id = document.id)
                    liveElectionsList.add(election)
                }
                liveAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                // Handle error
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
