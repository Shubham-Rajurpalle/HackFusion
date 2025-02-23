package com.cricketapp.hackfusion

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.databinding.ActivityItemComplaintBinding

class ComplaintAdapter(
    private var complaints: List<Complaint>,
    private val isDean: Boolean = false,
    private val onStatusUpdate: ((Complaint, String) -> Unit)? = null
) : RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder>() {

    class ComplaintViewHolder(val binding: ActivityItemComplaintBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewHolder {
        val binding = ActivityItemComplaintBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ComplaintViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        val complaint = complaints[position]

        with(holder.binding) {
            tvSection.text = "Section: ${complaint.section}"
            tvDetails.text = complaint.details
            tvDateTime.text = complaint.timestamp
            tvComplaintStatus.text = complaint.status

            // Set status color
            val statusColor = when(complaint.status) {
                "Accepted" -> Color.GREEN
                "Rejected" -> Color.RED
                else -> Color.parseColor("#FFA500") // Orange for pending
            }
            tvComplaintStatus.setTextColor(statusColor)

            // Show/hide admin buttons based on role and current status
            adminButtons.visibility = if (isDean && complaint.status == "Pending")
                View.VISIBLE else View.GONE

            btnApprove.setOnClickListener {
                onStatusUpdate?.invoke(complaint, "Accepted")
            }

            btnReject.setOnClickListener {
                onStatusUpdate?.invoke(complaint, "Rejected")
            }
        }
    }

    override fun getItemCount() = complaints.size

    fun updateList(newComplaints: List<Complaint>) {
        complaints = newComplaints
        notifyDataSetChanged()
    }
}