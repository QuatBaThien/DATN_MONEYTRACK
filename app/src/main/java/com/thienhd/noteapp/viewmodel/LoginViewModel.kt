package com.thienhd.noteapp.viewmodel


import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    fun login() {
        val emailValue = email.value
        val passwordValue = password.value

        if (!emailValue.isNullOrEmpty() && !passwordValue.isNullOrEmpty()) {
            auth.signInWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(getApplication(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                        _loginSuccess.value = true
                    } else {
                        Toast.makeText(getApplication(), "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(getApplication(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
        }
    }
}
