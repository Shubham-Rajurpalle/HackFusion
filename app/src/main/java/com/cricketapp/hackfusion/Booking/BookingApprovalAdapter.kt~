package com.cricketapp.hackfusion

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class BookingApprovalAdapter(
    private var bookings: List<Booking>,
    private val isDean: Boolean,
    private val onApprovalAction: (Booking, Boolean) -> Unit
) : RecyclerView.Adapter<BookingApprovalAdapter.BookingViewHolder>() {

    class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUserName: TextView = view.findViewById(R.id.tvUserName)
        val tvFacilityName: TextView = view.findViewById(R.id.tvFacilityName)
        val tvPurpose: TextView = view.findViewById(R.id.tvPurpose)
        val tvTimeSlot: TextView = view.findViewById(R.id.tvTimeSlot)
        val tvApprovalStatus: TextView = view.findViewById(R.id.tvApprovalStatus)
        val adminButtons: LinearLayout = view.findViewById(R.id.adminButtons)
        val btnApprove: Button = view.findViewById(R.id.btnApprove)
        val btnReject: Button = view.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        val context = holder.itemView.context

        // Set basic booking information
        holder.tvUserName.text = "Booked By: ${booking.bookedBy}"
        holder.tvFacilityName.text = booking.facility
        holder.tvPurpose.text = booking.purpose
        holder.tvTimeSlot.text = "${booking.startTime} - ${booking.endTime}"

        // Update status and UI based on booking state
        updateBookingStatus(holder, booking)

        // Show/hide admin buttons based on isDean and booking status
        if (isDean && !booking.approved) {
            holder.adminButtons.visibility = View.VISIBLE

            holder.btnApprove.setOnClickListener {
                onApprovalAction(booking, true)
                // Update the local booking object
                booking.approved = true
                // Update the UI immediately
                updateBookingStatus(holder, booking)
                // Hide the buttons
                holder.adminButtons.visibility = View.GONE
            }

            holder.btnReject.setOnClickListener {
                onApprovalAction(booking, false)
                // Update the local booking object to show rejected state
                booking.approved = false
                // Update the UI immediately
                updateBookingStatus(holder, booking)
                // Hide the buttons after rejection
                holder.adminButtons.visibility = View.GONE
            }
        } else {
            holder.adminButtons.visibility = View.GONE
        }
    }

    private fun updateBookingStatus(holder: BookingViewHolder, booking: Booking) {
        when {
            booking.approved -> {
                holder.tvApprovalStatus.text = "Approved"
                holder.tvApprovalStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
                holder.adminButtons.visibility = View.GONE
            }
            else -> {
                holder.tvApprovalStatus.text = "Pending"
                holder.tvApprovalStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
                // Only show buttons if it's a dean and the booking is pending
                holder.adminButtons.visibility = if (isDean) View.VISIBLE else View.GONE
            }
        }
    }

    override fun getItemCount() = bookings.size

    // Add method to update the bookings list
    fun updateBookings(newBookings: List<Booking>) {
        bookings = newBookings
        notifyDataSetChanged()
    }
}