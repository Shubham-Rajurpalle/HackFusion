package com.cricketapp.hackfusion.Election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cricketapp.hackfusion.R
import com.cricketapp.hackfusion.databinding.FragmentResultViewBinding

class result_view : Fragment() {

    private var _binding: FragmentResultViewBinding? = null
    private val binding get() = _binding!!

    private lateinit var election: Election

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        election = arguments?.getParcelable("election") ?: return

        binding.tvElectionName.text = election.electionName

        // Calculate total votes
        val totalVotes = election.votes.values.sum()
        binding.tvTotalVotes.text = "Total Votes: $totalVotes"

        // Check if election has ended
        val currentTime = System.currentTimeMillis()
        if (election.endTime < currentTime) {
            // Determine winner
            val winner = election.votes.maxByOrNull { it.value }?.key ?: "No winner"
            binding.tvWinnerName.text = "Winner: $winner"
        } else {
            binding.tvWinnerName.text = "Election is Live"
        }

        binding.btnBackToElections.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.navHost, ElectionFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.rvCandidates.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCandidates.adapter = ResultsAdapter(election.votes)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
