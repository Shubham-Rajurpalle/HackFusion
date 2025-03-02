package com.cricketapp.hackfusion.Election

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.R

class CandidateFacultyAdapter(
    private val candidates: List<String>,
    private val votes: Map<String, Int> // Votes for each candidate
) : RecyclerView.Adapter<CandidateFacultyAdapter.CandidateViewHolder>() {

    class CandidateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val candidateName: TextView? = itemView.findViewById(R.id.tvCandidateName)
        val candidateVotes: TextView? = itemView.findViewById(R.id.tvCandidateVotes)

        init {
            if (candidateName == null) {
                Log.e("CandidateFacultyAdapter", "tvCandidateName is NULL! Check item_candidate.xml")
            }
            if (candidateVotes == null) {
                Log.e("CandidateFacultyAdapter", "tvCandidateVotes is NULL! Check item_candidate.xml")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_candidate_faculty, parent, false)
        Log.d("CandidateFacultyAdapter", "Inflating item_candidate.xml")
        return CandidateViewHolder(view)
    }

    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        val candidate = candidates[position]
        holder.candidateName?.text = candidate
        holder.candidateVotes?.text = "Votes: ${votes[candidate] ?: 0}"
    }

    override fun getItemCount(): Int = candidates.size
}
