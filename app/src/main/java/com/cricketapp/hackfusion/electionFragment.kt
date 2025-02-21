package com.cricketapp.hackfusion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cricketapp.hackfusion.databinding.FragmentElectionBinding
import com.google.firebase.firestore.FirebaseFirestore

class ElectionFragment : Fragment() {
    private var _binding: FragmentElectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var previousAdapter: PreviousElectionsAdapter
    private lateinit var liveAdapter: LiveElectionsAdapter

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
        previousAdapter=PreviousElectionsAdapter(previousElectionsList,
            {election: Election ->
                
            })
        binding.recyclerPreviousElections.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPreviousElections.adapter = previousAdapter

        binding.recyclerLiveElections.layoutManager = LinearLayoutManager(requireContext())
        liveAdapter = LiveElectionsAdapter(liveElectionsList,
            onVoteClick = { electionId, candidateName ->
                voteForCandidate(electionId, candidateName)
            },
            onViewResultClick = { election ->
                // Handle view results
            }
        )
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
            .addOnFailureListener { e ->
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
            .addOnFailureListener { e ->
                // Handle error
            }
    }

    private fun voteForCandidate(electionId: String, candidateName: String) {
        val electionRef = db.collection("elections").document(electionId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(electionRef)
            val votes = snapshot.get("votes") as MutableMap<String, Long>

            val updatedVotes = (votes[candidateName] ?: 0) + 1
            votes[candidateName] = updatedVotes

            transaction.update(electionRef, "votes", votes)
        }.addOnSuccessListener {
            fetchLiveElections() // Refresh list after voting
        }.addOnFailureListener {
            // Handle error
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
