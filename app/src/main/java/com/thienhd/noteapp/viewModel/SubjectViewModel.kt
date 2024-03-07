package com.thienhd.noteapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SubjectViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is subject Fragment"
    }
    val text: LiveData<String> = _text
}