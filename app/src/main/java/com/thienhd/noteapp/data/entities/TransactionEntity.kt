package com.thienhd.noteapp.data.entities

import com.google.firebase.Timestamp

data class Transaction(
    var transactionID: String = "",
    var walletID: String = "",
    var categoryID: Int = 0,
    var note: String = "",
    var type: Int = 0,
    var amount: Double = 0.0,
    var date: Timestamp = Timestamp.now(),
    var hour: String = "",
    var userID: String = "",
    var isDaytitle: Boolean = false
) {
}
