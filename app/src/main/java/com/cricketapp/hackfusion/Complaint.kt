package com.cricketapp.hackfusion

data class Complaint(
    var id: String = "",
    val section: String = "",
    val details: String = "",
    val timestamp: String = "",
    var status: String = "Pending"  // "Pending", "Accepted", or "Rejected"
)
