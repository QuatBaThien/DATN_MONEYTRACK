package com.thienhd.noteapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChooseWalletViewModel : ViewModel() {

    private val _walletId = MutableLiveData<String?>()
    val walletId: LiveData<String?> get() = _walletId

    fun setWalletId(walletId: String) {
        _walletId.value = walletId
    }

    fun resetWallet() {
        _walletId.value = null
    }
}
