package com.cricketapp.hackfusion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.models.LeaveApplication

class LeaveApplicationAdapter(private var applications: List<LeaveApplication>) :
    RecyclerView.Adapter<LeaveApplicationAdapter.ViewHolder>() {

    fun updateData(newList: List<LeaveApplication>) {
        applications = newList
        notifyDataSetChanged() // Refresh RecyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leave_application, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val application = applications[position]
        holder.bind(application)
    }

    override fun getItemCount(): Int = applications.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        private val tvRegNo: TextView = itemView.findViewById(R.id.tvRegNo)
        private val tvLeaveType: TextView = itemView.findViewById(R.id.tvLeaveType)
        private val tvReason: TextView = itemView.findViewById(R.id.tvReason)

        fun bind(application: LeaveApplication) {
            tvStudentName.text = application.name
            tvRegNo.text = "Reg No: ${application.rollNumber}"
            tvLeaveType.text = "Leave Type: ${application.leaveType}"
            tvReason.text = "Reason: ${application.reason}"
        }
    }
}
