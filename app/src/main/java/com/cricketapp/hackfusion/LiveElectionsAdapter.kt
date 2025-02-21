package com.cricketapp.hackfusion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.databinding.ItemLiveElectionBinding
import java.util.concurrent.TimeUnit

class LiveElectionsAdapter(
    private val liveElections: List<Election>,
    private val onVoteClick: (String, String) -> Unit, // Takes electionId & candidateName
    private val onViewResultClick: (Election) -> Unit
) : RecyclerView.Adapter<LiveElectionsAdapter.LiveElectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveElectionViewHolder {
        val binding = ItemLiveElectionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LiveElectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LiveElectionViewHolder, position: Int) {
        holder.bind(liveElections[position])
    }

    override fun getItemCount(): Int = liveElections.size

    inner class LiveElectionViewHolder(private val binding: ItemLiveElectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(election: Election) {
            binding.tvElectionName.text = election.electionName
            binding.tvElectionStatus.text = "Ending in ${getTimeRemaining(election.endTime)}"

            binding.rvCandidates.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvCandidates.adapter = CandidatesAdapter(
                election.candidates,  // Pass list of candidates
                election.votes,       // Pass votes map
                onVoteClick = { candidateName ->
                    onVoteClick(election.id, candidateName)
                }
            )

            binding.btnViewResult.setOnClickListener {
                onViewResultClick(election)
            }
        }

        private fun getTimeRemaining(endTime: Long): String {
            val currentTime = System.currentTimeMillis()
            val timeLeft = endTime - currentTime

            return if (timeLeft > 0) {
                val hours = TimeUnit.MILLISECONDS.toHours(timeLeft)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % 60
                "$hours hrs $minutes min"
            } else {
                "Ended"
            }
        }
    }
}
