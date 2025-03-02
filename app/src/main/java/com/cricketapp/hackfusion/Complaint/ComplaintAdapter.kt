package com.cricketapp.hackfusion.Complaint

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.R

class ComplaintAdapter(private var complaintList: List<Complaint>) :
    RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder>() {

    class ComplaintViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSection: TextView = itemView.findViewById(R.id.tvSection)
        val tvDetails: TextView = itemView.findViewById(R.id.tvDetails)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_complaint, parent, false)  // ✅ Use the correct item layout
        return ComplaintViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        val complaint = complaintList[position]
        holder.tvSection.text = "Section: ${complaint.section}"
        holder.tvDetails.text = "Details: ${complaint.details}"
        holder.tvDateTime.text = "Date & Time: ${complaint.timestamp}"  // ✅ Corrected field name
    }

    override fun getItemCount(): Int {
        return complaintList.size
    }

    fun updateList(newList: List<Complaint>) {
        complaintList = newList
        notifyDataSetChanged()
    }
}