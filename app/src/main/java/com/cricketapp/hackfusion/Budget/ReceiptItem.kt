package com.cricketapp.hackfusion.Budget


data class ReceiptItem(
    val id: String,
    val imageUrl: String,
    val amount: Float,
    val vendor: String,
    val date: String,
    val description: String
)