package com.thienhd.noteapp.data.entities

import java.util.Date

data class Budget(
    var budgetID: String = "",
    val userID: String = "",
    val categoryID: Int = 0,
    var targetAmount: Double = 0.0,
    var currentAmount: Double = 0.0,
    val dateStart: Date? = null,
    val dateEnd: Date? = null
)
