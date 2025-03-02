package com.cricketapp.hackfusion.Election

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.R


class CandidatesAdapter(
    private val candidates: List<String>,
    private val votes: Map<String, Int>,
    private var hasVoted: Boolean,  // Make this mutable
    private val onVoteClick: (String) -> Unit
) : RecyclerView.Adapter<CandidatesAdapter.CandidateViewHolder>() {

    inner class CandidateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCandidateName: TextView = itemView.findViewById(R.id.tvCandidateName)
        val btnVote: Button = itemView.findViewById(R.id.btnVote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_candidate, parent, false)
        return CandidateViewHolder(view)
    }

    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        val candidateName = candidates[position]
        holder.tvCandidateName.text = candidateName

        holder.btnVote.isEnabled = !hasVoted
        holder.btnVote.alpha = if (hasVoted) 0.5f else 1.0f // Reduce opacity when disabled

        holder.btnVote.setOnClickListener {
            if (!hasVoted) {
                onVoteClick(candidateName)

                // **Update hasVoted to prevent multiple votes**
                hasVoted = true
                notifyDataSetChanged() // Refresh the entire list
            }
        }
    }

    override fun getItemCount(): Int = candidates.size
}
