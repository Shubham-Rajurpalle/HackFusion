package com.cricketapp.hackfusion

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.databinding.ActivityElectionCardBinding
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ElectionAdapter(
    private var elections: List<Election>,
    private val onElectionClick: (Election) -> Unit
) : RecyclerView.Adapter<ElectionAdapter.ElectionViewHolder>() {

    inner class ElectionViewHolder(private val binding: ActivityElectionCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(election: Election) {
            binding.tvElectionName.text = election.electionName
            binding.chipStatus.text = if (election.status) "Active" else "Closed"
            binding.chipStatus.setChipBackgroundColorResource(
                if (election.status) R.color.green else R.color.red
            )

            binding.chipGroupCandidates.removeAllViews()
            election.candidates.forEach { candidate ->
                val chip = Chip(binding.chipGroupCandidates.context).apply {
                    text = candidate
                    isClickable = false
                    isCheckable = false
                }
                binding.chipGroupCandidates.addView(chip)
            }

            // Calculate total votes
            val totalVotes = election.votes.values.sum()
            binding.tvTotalVotes.text = "Total Votes: $totalVotes"

            // Time Remaining
            binding.timeRemaining.text = "Time Remaining: ${getTimeRemaining(election.startTime, election.endTime)}"

            binding.root.setOnClickListener { onElectionClick(election) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        val binding = ActivityElectionCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ElectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        holder.bind(elections[position])
    }

    override fun getItemCount() = elections.size

    fun updateList(newElections: List<Election>) {
        elections = newElections
        notifyDataSetChanged()
    }

    private fun getTimeRemaining(startTime: Long, endTime: Long): String {
        val currentTime = System.currentTimeMillis()

        return when {
            currentTime < startTime -> {
                val remainingMillis = startTime - currentTime
                val hours = TimeUnit.MILLISECONDS.toHours(remainingMillis)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60
                "Starts in $hours hours $minutes minutes"
            }
            currentTime in startTime..endTime -> {
                val remainingMillis = endTime - currentTime
                val hours = TimeUnit.MILLISECONDS.toHours(remainingMillis)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60
                "$hours hours $minutes minutes left"
            }
            else -> "Voting ended"
        }
    }


}