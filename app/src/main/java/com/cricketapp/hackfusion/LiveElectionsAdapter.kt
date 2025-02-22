package com.cricketapp.hackfusion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.databinding.ItemLiveElectionBinding
import java.util.concurrent.TimeUnit

class LiveElectionsAdapter(
    private val liveElections: List<Election>,
    private val onVoteClick: (String, String) -> Unit, // electionId, candidateName
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

            val currentUserId = "34567986"// Get logged-in user ID
            val hasVoted = election.voters.containsKey(currentUserId)

            binding.rvCandidates.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvCandidates.adapter =
                _root_ide_package_.com.cricketapp.hackfusion.CandidatesAdapter(
                    election.candidates,
                    election.votes,
                    hasVoted, // Disable vote button if already voted
                    onVoteClick = { candidateName ->
                        if (!hasVoted) {
                            onVoteClick(election.id, candidateName)
                        }
                    }
                )

            binding.btnViewResult.setOnClickListener {
                val fragment = ViewResultFragment()
                val bundle = Bundle()
                bundle.putParcelable("election", election) // Pass election data
                fragment.arguments = bundle

                val activity = binding.root.context as AppCompatActivity
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.result_view_container, fragment)
                    .addToBackStack(null)
                    .commit()
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
