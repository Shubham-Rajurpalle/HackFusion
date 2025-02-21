package com.cricketapp.hackfusion

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class facilityFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_facility, container, false)

        // Initialize Buttons
        val btnCheckStatus = view.findViewById<Button>(R.id.btnCheckStatus)
        val btnBookFacility = view.findViewById<Button>(R.id.btnBookFacility)

        // Navigate to Booking Status Activity
        btnCheckStatus.setOnClickListener {
            val intent = Intent(requireContext(), facilityStatus::class.java)
            startActivity(intent)
        }

        // Navigate to Facility Booking Activity
        btnBookFacility.setOnClickListener {
            val intent = Intent(requireContext(), booking_facility::class.java)
            startActivity(intent)
        }

        return view
    }
}