package com.personal.program.expensetracker.model

data class Transaction(
    val transactionId: String = "",
    val amount: Double = 0.0,
    val type: String = "",
    val category: String = "",
    val date: String = "",
    val description: String = ""
)
