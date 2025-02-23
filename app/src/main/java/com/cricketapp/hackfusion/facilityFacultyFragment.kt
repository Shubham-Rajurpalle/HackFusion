package com.cricketapp.hackfusion

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.media3.common.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class facilityFacultyFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingApprovalAdapter
    private val bookingList = ArrayList<Booking>()
    private lateinit var databaseRef: DatabaseReference
    private var isDean: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_facility_faculty, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewBookings)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        // Add debug Toast
        Toast.makeText(requireContext(), "Fragment Created", Toast.LENGTH_SHORT).show()

//        checkIfUserIsDean { isDeanResult ->
//            isDean = isDeanResult
//            Log.d("FacultyFragment", "User isDean status: $isDean")
//            Toast.makeText(requireContext(), "Is Dean: $isDean", Toast.LENGTH_SHORT).show()
//
//        }
        fetchBookingsFromFirebase()

        return view
    }

    private fun checkIfUserIsDean(callback: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.d("FacultyFragment", "No user ID found")
            callback(false)
            return
        }

        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val role = snapshot.child("role").getValue(String::class.java)
                Log.d("FacultyFragment", "User role: $role")
                callback(role == "Dean")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FacultyFragment", "Error checking dean status", error.toException())
                callback(false)
            }
        })
    }

    private fun fetchBookingsFromFirebase() {
        Log.d("FacultyFragment", "Fetching bookings, isDean: $isDean")
        databaseRef = FirebaseDatabase.getInstance().reference.child("Bookings")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookingList.clear()
                for (bookingSnapshot in snapshot.children) {
                    val booking = bookingSnapshot.getValue(Booking::class.java)
                    booking?.let {
                        it.id = bookingSnapshot.key.toString()
                        bookingList.add(it)
                    }
                }

                Log.d("FacultyFragment", "Loaded ${bookingList.size} bookings")
                Toast.makeText(context, "Loaded ${bookingList.size} bookings", Toast.LENGTH_SHORT).show()

                bookingAdapter = BookingApprovalAdapter(bookingList, isDean) { booking, isApproved ->
                    updateBookingStatus(booking, isApproved)
                }
                recyclerView.adapter = bookingAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FacultyFragment", "Error loading bookings", error.toException())
                Toast.makeText(context, "Failed to load bookings", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateBookingStatus(booking: Booking, isApproved: Boolean) {
        val bookingRef = FirebaseDatabase.getInstance().reference
            .child("Bookings")
            .child(booking.id)

        val updates = hashMapOf<String, Any>(
            "approved" to isApproved
        )

        bookingRef.updateChildren(updates)
            .addOnSuccessListener {
                val status = if (isApproved) "approved" else "rejected"
                Toast.makeText(requireContext(), "Booking $status successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}