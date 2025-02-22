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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class facilityFacultyFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingApprovalAdapter
    private val bookingList = ArrayList<Booking>()
    private lateinit var databaseRef: DatabaseReference
    private var isDean: Boolean = false // Default false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_facility_faculty, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewBookings)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        // 🔥 Step 1: Fetch `isDean` first, then fetch bookings
        checkIfUserIsDean { isDeanResult ->
            isDean = isDeanResult
            fetchBookingsFromFirebase()
        }

        return view
    }

    private fun checkIfUserIsDean(callback: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return callback(false)
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val role = snapshot.child("role").getValue(String::class.java)
                callback(role == "dean") // If role is "dean", return true
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }

    private fun fetchBookingsFromFirebase() {
        databaseRef = FirebaseDatabase.getInstance().reference.child("Bookings")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookingList.clear()
                for (bookingSnapshot in snapshot.children) {
                    val booking = bookingSnapshot.getValue(Booking::class.java)
                    booking?.let {
                        it.id = bookingSnapshot.key.toString() // Set Firebase key as ID
                        bookingList.add(it)
                    }
                }

                // 🔥 Initialize adapter AFTER getting `isDean`
                bookingAdapter = BookingApprovalAdapter(bookingList, isDean) { booking, isApproved ->
                    updateBookingStatus(booking, isApproved)
                }
                recyclerView.adapter = bookingAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load bookings", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateBookingStatus(booking: Booking, isApproved: Boolean) {
        val bookingRef = FirebaseDatabase.getInstance().reference.child("Bookings").child(booking.id)

        bookingRef.child("approved").setValue(isApproved)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Booking updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
            }
    }
}
