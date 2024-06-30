package com.thienhd.noteapp.data.entities

data class Category(
    val categoryID: Int = 0,
    var iconId: Int = 0,
    var name: String = "",
    var type: Int = 0, // 0 for income, 1 for expense
    val userID: String = ""
)