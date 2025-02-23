package com.cricketapp.hackfusion

data class Booking(
    var id: String = "",  // Unique Firebase ID (nullable)
    val facility: String = "",
    val purpose: String = "",
    val startTime: String = "",
    val endTime: String = "",
    var approved: Boolean = false,
    val bookedBy: String = ""
) {
    constructor() : this("", "", "", "", "", false, "")
}
