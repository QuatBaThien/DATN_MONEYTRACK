package com.thienhd.noteapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thienhd.noteapp.data.entities.Debt
import com.thienhd.noteapp.data.entities.Loan
import com.thienhd.noteapp.data.entities.Transaction

class DebtViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _debts = MutableLiveData<List<Debt>>()
    val debts: LiveData<List<Debt>> get() = _debts

    private val _loans = MutableLiveData<List<Loan>>()
    val loans: LiveData<List<Loan>> get() = _loans

    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    fun loadDebts() {
        val userId = getCurrentUserId()
        db.collection("debts")
            .whereEqualTo("userID", userId)
            .get()
            .addOnSuccessListener { documents ->
                val debtList = documents.map { document ->
                    val debt = document.toObject(Debt::class.java)
                    debt.copy(debtID = document.id)
                }
                _debts.postValue(debtList)
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }

    fun loadLoans() {
        val userId = getCurrentUserId()
        db.collection("loans")
            .whereEqualTo("userID", userId)
            .get()
            .addOnSuccessListener { documents ->
                val loanList = documents.map { document ->
                    val loan = document.toObject(Loan::class.java)
                    loan.copy(loanID = document.id)
                }
                _loans.postValue(loanList)
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }
    fun addDebt(debt: Debt) {
        db.collection("debts")
            .add(debt)
            .addOnSuccessListener {
                loadDebts()
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }

    fun addLoan(loan: Loan) {
        db.collection("loans")
            .add(loan)
            .addOnSuccessListener {
                loadLoans()
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }

    fun getDebtById(id: String, type: String): Any? {
        return if (type == "debt") _debts.value?.find { it.debtID == id } else _loans.value?.find { it.loanID == id }
    }

    fun updateDebt(debt: Debt) {
        db.collection("debts").document(debt.debtID)
            .set(debt)
            .addOnSuccessListener {
                loadDebts()
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }

    fun updateLoan(loan: Loan) {
        db.collection("loans").document(loan.loanID)
            .set(loan)
            .addOnSuccessListener {
                loadLoans()
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }
}
