package com.cricketapp.hackfusion.Application


data class LeaveApplication(
    var id: String = "",
    var userId: String = "",
    var studentName: String = "",
    var regNo: String = "",
    var leaveType: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var reason: String = "",
    var status: String = "Pending",
    var timestamp: Long = 0L
)