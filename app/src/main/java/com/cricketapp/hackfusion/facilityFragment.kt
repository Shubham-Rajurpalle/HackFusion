package com.cricketapp.hackfusion

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cricketapp.hackfusion.Booking
import com.google.firebase.database.*

class facilityFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter
    private lateinit var bookingList: ArrayList<Booking>
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_facility, container, false)

        // Initialize Buttons
        val btnBookFacility = view.findViewById<Button>(R.id.btnBookFacility)

        // Navigate to Facility Booking Activity
        btnBookFacility.setOnClickListener {
            val intent = Intent(requireContext(), com.cricketapp.hackfusion.booking_facility::class.java)
            startActivity(intent)
        }

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewBookings)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        bookingList = arrayListOf()
        bookingAdapter = BookingAdapter(bookingList)
        recyclerView.adapter = bookingAdapter

        // Fetch data from Firebase
        fetchBookingsFromFirebase()

        return view
    }

    private fun fetchBookingsFromFirebase() {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("Bookings")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookingList = mutableListOf<Booking>()

                for (bookingSnapshot in snapshot.children) {
                    val booking = bookingSnapshot.getValue(Booking::class.java)
                    booking?.let { bookingList.add(it) }
                }

                bookingAdapter = BookingAdapter(bookingList)
                recyclerView.adapter = bookingAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load bookings", Toast.LENGTH_SHORT).show()
            }
        })
    }
}