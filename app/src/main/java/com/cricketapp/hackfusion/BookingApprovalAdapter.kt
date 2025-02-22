package com.cricketapp.hackfusion

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookingApprovalAdapter(
    private val bookings: List<Booking>,
    private val isDean: Boolean, // Check if the user is a Dean
    private val onStatusUpdate: (Booking, Boolean) -> Unit
) : RecyclerView.Adapter<BookingApprovalAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvFacilityName: TextView = itemView.findViewById(R.id.tvFacilityName)
        val tvPurpose: TextView = itemView.findViewById(R.id.tvPurpose)
        val tvTimeSlot: TextView = itemView.findViewById(R.id.tvTimeSlot)
        val tvApprovalStatus: TextView = itemView.findViewById(R.id.tvApprovalStatus)
        val adminButtons: LinearLayout = itemView.findViewById(R.id.adminButtons)
        val btnApprove: Button = itemView.findViewById(R.id.btnApprove)
        val btnReject: Button = itemView.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]

        holder.tvUserName.text = "Booked By: ${booking.bookedBy}"
        holder.tvFacilityName.text = booking.facility
        holder.tvPurpose.text = booking.purpose
        holder.tvTimeSlot.text = "${booking.startTime} - ${booking.endTime}"
        holder.tvApprovalStatus.text = if (booking.approved) "Approved" else "Not Approved"
        holder.tvApprovalStatus.setTextColor(if (booking.approved) Color.GREEN else Color.RED)

        // 🔥 FIX: Make admin buttons visible if the user is a Dean
        holder.adminButtons.visibility = if (isDean) View.VISIBLE else View.GONE

        // 🔥 FIX: Ensure buttons are enabled & clickable
        holder.btnApprove.isEnabled = isDean
        holder.btnReject.isEnabled = isDean

        // Handle Approve Button Click
        holder.btnApprove.setOnClickListener {
            if (isDean) onStatusUpdate(booking, true)
        }

        // Handle Reject Button Click
        holder.btnReject.setOnClickListener {
            if (isDean) onStatusUpdate(booking, false)
        }
    }

    override fun getItemCount(): Int = bookings.size
}
