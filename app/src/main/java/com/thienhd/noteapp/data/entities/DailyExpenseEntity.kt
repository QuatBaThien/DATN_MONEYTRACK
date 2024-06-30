package com.thienhd.noteapp.data.entities




data class DailyExpense(
    val dailyExpenseID: Int,
    val reportID: Int,
    val amount: Double,
    val date: String
)