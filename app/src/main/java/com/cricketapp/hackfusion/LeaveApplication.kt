package com.cricketapp.hackfusion.models

data class LeaveApplication(
    var name: String = "",
    var rollNumber: String = "",
    var leaveType: String = "",
    var reason: String = "",
    var timestamp: Long = 0L
)
