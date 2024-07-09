package com.thienhd.noteapp.data.entities

import com.google.firebase.Timestamp


data class Debt(
    val debtID: String ="",
    val userID: String ="",
    val lender: String ="",
    val amount: Double = 0.0,
    val paidAmount: Double = 0.0,
    var dateBorrowed: Timestamp = Timestamp.now(),
    var dateDue: Timestamp = Timestamp.now(),
    var isUndeadline: Int = 0
)