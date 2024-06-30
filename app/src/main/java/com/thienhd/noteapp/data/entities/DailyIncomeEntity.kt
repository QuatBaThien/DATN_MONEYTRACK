package com.thienhd.noteapp.data.entities



data class DailyIncome(
    val dailyIncomeID: Int,
    val reportID: Int,
    val amount: Double,
    val date: String
)