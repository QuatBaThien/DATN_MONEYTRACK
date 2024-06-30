package com.thienhd.noteapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


data class DebtPayment(
    val paymentID: Int,
    val debtID: Int,
    val amount: Double,
    val date: String
)