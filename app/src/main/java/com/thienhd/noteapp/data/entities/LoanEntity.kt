package com.thienhd.noteapp.data.entities

import com.google.firebase.Timestamp

data class Loan (
    val loanID: String ="",
    val userID: String ="",
    val borrower: String ="",
    val amount: Double = 0.0,
    val paidAmount: Double = 0.0,
    var dateBorrowed: Timestamp = Timestamp.now(),
    var dateDue: Timestamp = Timestamp.now(),
    var isUndeadline: Int = 0
)