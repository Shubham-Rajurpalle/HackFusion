package com.cricketapp.hackfusion.Booking

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.R

class BookingAdapter(private val bookingList: List<Booking>) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val facilityName: TextView = itemView.findViewById(R.id.tvFacilityName)
        val purpose: TextView = itemView.findViewById(R.id.tvPurpose)
        val timeSlot: TextView = itemView.findViewById(R.id.tvTimeSlot)
        val approvalStatus: TextView = itemView.findViewById(R.id.tvApprovalStatus)
        val userName: TextView = itemView.findViewById(R.id.tvUserName)  // Added user name field
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookingList[position]
        holder.facilityName.text = booking.facility
        holder.purpose.text = booking.purpose
        holder.timeSlot.text = "${booking.startTime} - ${booking.endTime}"
        holder.userName.text = "Booked By: ${booking.bookedBy}"

        // Set approval status color
        if (booking.approved) {
            holder.approvalStatus.text = "Approved"
            holder.approvalStatus.setTextColor(Color.GREEN)
        } else {
            holder.approvalStatus.text = "Not Approved"
            holder.approvalStatus.setTextColor(Color.RED)
        }
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }
}