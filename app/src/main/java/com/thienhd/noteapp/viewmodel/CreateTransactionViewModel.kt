package com.thienhd.noteapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateTransactionViewModel : ViewModel() {

    private val _categoryId = MutableLiveData<Int?>()
    val categoryId: LiveData<Int?> get() = _categoryId

    private val _walletId = MutableLiveData<String?>()
    val walletId: LiveData<String?> get() = _walletId

    fun setCategoryId(id: Int) {
        _categoryId.value = id
    }

    fun reset() {
        _categoryId.value = null
        _walletId.value = null
    }

    fun setWalletId(id: String) {
        _walletId.value = id
    }
}