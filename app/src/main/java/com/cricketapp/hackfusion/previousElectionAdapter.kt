package com.cricketapp.hackfusion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.databinding.ItemPreviousElectionBinding

class PreviousElectionsAdapter(
    private val elections: List<Election>,
    private val onViewResultClick: (Election) -> Unit
) : RecyclerView.Adapter<PreviousElectionsAdapter.ElectionViewHolder>() {

    inner class ElectionViewHolder(private val binding: ItemPreviousElectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(election: Election) {
            binding.tvPreviousElectionName.text = election.electionName
            binding.tvWinner.text = "Winner: ${election.winner ?: "Not Declared"}"

            // Log to check if button is being clicked
            binding.btnViewResult.setOnClickListener {
                val fragment = result_view()
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        val binding = ItemPreviousElectionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ElectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        holder.bind(elections[holder.adapterPosition])
    }

    override fun getItemCount(): Int = elections.size
}
