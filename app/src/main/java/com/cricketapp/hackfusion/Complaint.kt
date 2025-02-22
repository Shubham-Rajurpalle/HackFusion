package com.cricketapp.hackfusion

data class Complaint(
    val id: String = "",
    val section: String = "",  // Ensure 'section' is defined
    val details: String = "",
    val timestamp: String = ""
)