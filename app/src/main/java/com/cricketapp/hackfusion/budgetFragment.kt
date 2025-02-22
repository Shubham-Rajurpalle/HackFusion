package com.cricketapp.hackfusion


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class budgetFragment : Fragment() { // Renamed to follow Kotlin class naming conventions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find buttons
        val btnApplyLeave = view.findViewById<Button>(R.id.btnApplyLeave)
        val btnApplyApproval = view.findViewById<Button>(R.id.btnApplyApproval)

        // Ensure button is not null before setting click listener
        btnApplyLeave?.setOnClickListener {
            val intent = Intent(requireContext(), ApplyLeaveActivity::class.java)
            startActivity(intent)
        }

        btnApplyApproval?.setOnClickListener {
            val intent = Intent(requireContext(), ApplyApprovalActivity::class.java)
            startActivity(intent)
        }
    }
}