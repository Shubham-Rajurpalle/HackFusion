package com.cricketapp.hackfusion.Notification

data class Notification(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
