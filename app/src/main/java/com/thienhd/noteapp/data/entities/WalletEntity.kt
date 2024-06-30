package com.thienhd.noteapp.data.entities

data class Wallet(
    var balance: String = "",
    var isDeleted: Boolean = false,
    var name: String ="",
    val userID:String = "",
    var walletID: String =""
)