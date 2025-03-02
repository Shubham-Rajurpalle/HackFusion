package com.cricketapp.hackfusion.Budget

data class BudgetItem(
    val id: String,
    val title: String,
    val category: String,
    val amount: Float,
    val description: String,
    val date: String,
    val receipts: List<ReceiptItem>,
    val totalSpent: Double,
    val remaining: Double
)