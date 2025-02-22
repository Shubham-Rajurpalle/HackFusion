package com.cricketapp.hackfusion

data class Booking(
    val facility: String = "",
    val purpose: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val approved: Boolean = false,  // Default to false
    val bookedBy: String = ""  // Name of the user
) {
    // Required no-argument constructor for Firebase
    constructor() : this("", "", "", "", false, "")
}