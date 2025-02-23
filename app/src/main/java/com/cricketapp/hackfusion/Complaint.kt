package com.cricketapp.hackfusion

data class Complaint(
    var id: String = "",
    val filedBy: String = "",
    val section: String = "",
    val details: String = "",
    val timestamp: String = "",
    var approved: Boolean = false  // Default status
)

