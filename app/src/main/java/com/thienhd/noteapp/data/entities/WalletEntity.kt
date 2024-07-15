package com.thienhd.noteapp.data.entities

data class Wallet(
    var balance: Double = 0.0,
    var isDeleted: Boolean = false,
    var name: String ="",
    val userID:String = "",
    var walletID: String =""
)