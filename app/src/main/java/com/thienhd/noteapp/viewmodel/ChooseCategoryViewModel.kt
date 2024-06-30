package com.thienhd.noteapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChooseCategoryViewModel : ViewModel() {

    private val _categoryId = MutableLiveData<Int?>()
    val categoryId: LiveData<Int?> get() = _categoryId

    fun setCategoryId(categoryId: Int) {
        _categoryId.value = categoryId
    }

    fun resetCategory() {
        _categoryId.value = null
    }
}
