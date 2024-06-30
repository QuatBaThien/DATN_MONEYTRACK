package com.thienhd.noteapp.data.entities



data class Debt(
     val debtID: Int,
    val userID: Int,
    val lender: String,
    val amount: Double,
    val remainingAmount: Double,
    val dateBorrowed: String,
    val dateDue: String
)