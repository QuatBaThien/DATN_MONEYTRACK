package com.thienhd.noteapp.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    val fullName = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    var registrationSuccess = MutableLiveData<Boolean>()
    fun register() {
        val fullNameValue = fullName.value
        val emailValue = email.value
        val passwordValue = password.value
        val confirmPasswordValue = confirmPassword.value

        if (!fullNameValue.isNullOrEmpty() && !emailValue.isNullOrEmpty() && !passwordValue.isNullOrEmpty() && passwordValue == confirmPasswordValue) {
            auth.createUserWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = hashMapOf(
                            "fullName" to fullNameValue,
                            "email" to emailValue
                        )

                        db.collection("users").document(auth.currentUser!!.uid)
                            .set(user)
                            .addOnSuccessListener {
                                Toast.makeText(getApplication(), "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                               registrationSuccess.value = true
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(getApplication(), "Đăng ký thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        handleRegistrationError(task.exception)
                    }
                }
        } else {
            if (passwordValue != confirmPasswordValue) {
                Toast.makeText(getApplication(), "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(getApplication(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleRegistrationError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthUserCollisionException -> {
                Toast.makeText(getApplication(), "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show()
            }
            is FirebaseAuthWeakPasswordException -> {
                Toast.makeText(getApplication(), "Mật khẩu quá yếu", Toast.LENGTH_SHORT).show()
            }
            is FirebaseAuthInvalidCredentialsException -> {
                Toast.makeText(getApplication(), "Email không hợp lệ", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(getApplication(), "Đăng ký thất bại: ${exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
