package com.thienhd.noteapp.data.entities


data class Report(
     val reportID: Int,
    val userID: Int,
    val startDate: String,
    val endDate: String,
    val totalIncome: Double,
    val totalExpense: Double,
    val dailyExpenses: String?, // Reference to DailyExpense, can use List<DailyExpense> with TypeConverters
    val dailyIncomes: String?  // Reference to DailyIncome, can use List<DailyIncome> with TypeConverters
)
