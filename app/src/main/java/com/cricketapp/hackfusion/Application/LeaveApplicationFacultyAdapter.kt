package com.cricketapp.hackfusion.Application

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.R

class LeaveApplicationFacultyAdapter(
    private var leaveApplications: MutableList<LeaveApplication>,
    private val onStatusChange: (LeaveApplication, Boolean) -> Unit
) : RecyclerView.Adapter<LeaveApplicationFacultyAdapter.LeaveViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leave_application, parent, false)
        return LeaveViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaveViewHolder, position: Int) {
        holder.bind(leaveApplications[position])
    }

    override fun getItemCount(): Int = leaveApplications.size

    fun updateItem(updatedApplication: LeaveApplication) {
        val index = leaveApplications.indexOfFirst { it.id == updatedApplication.id }
        if (index != -1) {
            leaveApplications[index] = updatedApplication
            notifyItemChanged(index)  // Update UI only for the modified item
        }
    }

    inner class LeaveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        private val tvRegNo: TextView = itemView.findViewById(R.id.tvRegNo)
        private val tvLeaveType: TextView = itemView.findViewById(R.id.tvLeaveType)
        private val tvLeaveDate: TextView = itemView.findViewById(R.id.tvLeaveDate)
        private val tvReason: TextView = itemView.findViewById(R.id.tvReason)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val btnApprove: Button = itemView.findViewById(R.id.btnApprove)
        private val btnReject: Button = itemView.findViewById(R.id.btnReject)

        fun bind(application: LeaveApplication) {
            tvStudentName.text = application.studentName
            tvRegNo.text = "Reg No: ${application.regNo}"
            tvLeaveType.text = "Leave Type: ${application.leaveType}"
            tvLeaveDate.text = "From: ${application.startDate} To: ${application.endDate}"
            tvReason.text = "Reason: ${application.reason}"
            tvStatus.text = application.status

            if (application.status != "Pending") {
                btnApprove.visibility = View.INVISIBLE
                btnReject.visibility= View.INVISIBLE
            } else {
                btnApprove.isEnabled = true
                btnReject.isEnabled = true
            }

            btnApprove.setOnClickListener {
                onStatusChange(application, true) // Approve action
            }

            btnReject.setOnClickListener {
                onStatusChange(application, false) // Reject action
            }
        }
    }
}
