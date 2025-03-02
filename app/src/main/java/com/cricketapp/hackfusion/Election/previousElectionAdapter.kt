package com.cricketapp.hackfusion.Election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.R
import com.cricketapp.hackfusion.databinding.ItemPreviousElectionBinding

class PreviousElectionsAdapter(
    private val elections: List<Election>,
    private val onViewResultClick: (Election) -> Unit
) : RecyclerView.Adapter<PreviousElectionsAdapter.ElectionViewHolder>() {

    inner class ElectionViewHolder(private val binding: ItemPreviousElectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(election: Election) {
            val winnerName = election.votes.maxByOrNull { it.value }?.key ?: "Not Declared"

            binding.tvPreviousElectionName.text = election.electionName
            binding.tvWinner.text = "Winner: $winnerName"

            binding.btnViewResult.setOnClickListener {
                val fragment = result_view()
                val bundle = Bundle()
                bundle.putParcelable("election", election)
                fragment.arguments = bundle

                val activity = binding.root.context as AppCompatActivity
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.result_view_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        val binding = ItemPreviousElectionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ElectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        holder.bind(elections[position])
    }

    override fun getItemCount(): Int = elections.size
}
