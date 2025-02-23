package com.cricketapp.hackfusion

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class ComplaintApprovalAdapter(
    private var complaints: List<Complaint>, // Added type parameter
    private val isDean: Boolean,
    private val onApprovalAction: (Complaint, Boolean) -> Unit
) : RecyclerView.Adapter<ComplaintApprovalAdapter.ComplaintViewHolder>() { // Added ViewHolder type

    class ComplaintViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvComplaintStatus: TextView = view.findViewById(R.id.tvComplaintStatus)
        val adminButtons: LinearLayout = view.findViewById(R.id.adminButtons)
        val btnApprove: Button = view.findViewById(R.id.btnApprove)
        val btnReject: Button = view.findViewById(R.id.btnReject)
        val tvSection: TextView = view.findViewById(R.id.tvSection)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
        val tvDateTime: TextView = view.findViewById(R.id.tvDateTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_complaint, parent, false)
        return ComplaintViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        val complaint = complaints[position]

        holder.apply {
            tvSection.text = "Section: ${complaint.section}"
            tvDetails.text = "Details: ${complaint.details}"
            tvDateTime.text = "Date & Time: ${complaint.timestamp}"
        }

        // Update status and UI based on complaint state
        updateComplaintStatus(holder, complaint)

        // Show/hide admin buttons based on isDean and complaint status
        holder.adminButtons.visibility = if (isDean && !complaint.approved) View.VISIBLE else View.GONE

        if (isDean && !complaint.approved) {
            holder.btnApprove.setOnClickListener {
                onApprovalAction(complaint, true)
            }

            holder.btnReject.setOnClickListener {
                onApprovalAction(complaint, false)
            }
        }
    }

    private fun updateComplaintStatus(holder: ComplaintViewHolder, complaint: Complaint) {
        val (text, color) = if (complaint.approved) {
            "Resolved" to android.R.color.holo_green_dark
        } else {
            "Pending" to android.R.color.holo_red_dark
        }

        holder.tvComplaintStatus.apply {
            this.text = text
            setTextColor(context.getColor(color))
        }
    }

    override fun getItemCount() = complaints.size

    fun updateComplaints(newComplaints: List<Complaint>) {
        complaints = newComplaints
        notifyDataSetChanged()
        Log.d("ComplaintAdapter", "Adapter updated with ${newComplaints.size} complaints")
    }
}