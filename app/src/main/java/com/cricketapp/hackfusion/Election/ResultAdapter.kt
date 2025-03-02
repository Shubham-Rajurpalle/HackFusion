package com.cricketapp.hackfusion.Election

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.R

class ResultsAdapter(private val votes: Map<String, Int>) :
    RecyclerView.Adapter<ResultsAdapter.ResultViewHolder>() {

    inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCandidateName: TextView = itemView.findViewById(R.id.tvCandidateName)
        val tvVoteCount: TextView = itemView.findViewById(R.id.tvVoteCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val candidateName = votes.keys.toList()[position]
        val voteCount = votes[candidateName] ?: 0

        holder.tvCandidateName.text = candidateName
        holder.tvVoteCount.text = "$voteCount Votes"
    }

    override fun getItemCount(): Int = votes.size
}
