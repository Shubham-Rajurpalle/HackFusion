package com.cricketapp.hackfusion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CandidatesAdapter(
    private val candidates: List<String>,
    private val votes: Map<String, Int>,
    private val hasVoted: Boolean,  // Receive voting status
    private val onVoteClick: (String) -> Unit
) : RecyclerView.Adapter<com.cricketapp.hackfusion.CandidatesAdapter.CandidateViewHolder>() {

    inner class CandidateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCandidateName: TextView = itemView.findViewById(_root_ide_package_.com.cricketapp.hackfusion.R.id.tvCandidateName)
        val btnVote: Button = itemView.findViewById(_root_ide_package_.com.cricketapp.hackfusion.R.id.btnVote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): _root_ide_package_.com.cricketapp.hackfusion.CandidatesAdapter.CandidateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(_root_ide_package_.com.cricketapp.hackfusion.R.layout.item_candidate, parent, false)
        return CandidateViewHolder(view)
    }

    override fun onBindViewHolder(holder: _root_ide_package_.com.cricketapp.hackfusion.CandidatesAdapter.CandidateViewHolder, position: Int) {
        val candidateName = candidates[position]
        holder.tvCandidateName.text = candidateName

        if (hasVoted) {
            // Disable the Vote button if the user has already voted
            holder.btnVote.isEnabled = false
            holder.btnVote.alpha = 0.5f // Reduce opacity for visual feedback
        } else {
            holder.btnVote.isEnabled = true
            holder.btnVote.alpha = 1.0f
            holder.btnVote.setOnClickListener {
                onVoteClick(candidateName)
            }
        }
    }

    override fun getItemCount(): Int = candidates.size
}
