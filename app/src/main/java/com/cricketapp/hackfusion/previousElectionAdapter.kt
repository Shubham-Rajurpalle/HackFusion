package com.cricketapp.hackfusion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.databinding.ItemPreviousElectionBinding

class PreviousElectionsAdapter(
    private val elections: List<Election>,
    private val onViewResultClick: (Election) -> Unit
) : RecyclerView.Adapter<PreviousElectionsAdapter.ElectionViewHolder>() {

    class ElectionViewHolder(private val binding: ItemPreviousElectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(election: Election, onViewResultClick: (Election) -> Unit) {
            binding.tvPreviousElectionName.text = election.electionName
            binding.tvWinner.text = "Winner: ${election.winner}"
            binding.btnViewResult.setOnClickListener { onViewResultClick(election) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        val binding = ItemPreviousElectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ElectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        holder.bind(elections[position], onViewResultClick)
    }

    override fun getItemCount(): Int = elections.size
}
