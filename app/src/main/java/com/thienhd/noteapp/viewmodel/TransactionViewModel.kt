package com.thienhd.noteapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thienhd.noteapp.data.entities.Transaction
import com.thienhd.noteapp.data.DateTransaction
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private val _dateTransactions = MutableLiveData<List<DateTransaction>>()
    val dateTransactions: LiveData<List<DateTransaction>> get() = _dateTransactions

    init {
        viewModelScope.launch {
            loadTransactionsFromFirestore()
        }
    }

    private fun loadTransactionsFromFirestore() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("transactions")
            .whereEqualTo("userID", userId)
            .get()
            .addOnSuccessListener { documents ->
                val transactionsList = if (documents.isEmpty) {
                    listOf()
                } else {
                    documents.map { document ->
                        document.toObject(Transaction::class.java)
                    }
                }
                _transactions.value = transactionsList
            }
            .addOnFailureListener { exception ->
                Log.e("TransactionViewModel", "Error getting documents: ", exception)
            }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val transactionData = hashMapOf(
                "walletID" to transaction.walletID,
                "userID" to userId,
                "categoryID" to transaction.categoryID,
                "amount" to transaction.amount,
                "note" to transaction.note,
                "type" to transaction.type,
                "date" to transaction.date,
                "hour" to transaction.hour
            )

            db.collection("transactions").add(transactionData)
                .addOnSuccessListener {
                    loadTransactionsFromFirestore()
                }
                .addOnFailureListener { exception ->
                    Log.e("TransactionViewModel", "Error adding document: ", exception)
                }
        }
    }

    fun getTransactionById(id: String): Transaction? {
        return _transactions.value?.find { it.transactionID == id }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            db.collection("transactions")
                .whereEqualTo("userID", userId)
                .whereEqualTo("transactionID", id)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        db.collection("transactions").document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                loadTransactionsFromFirestore()
                            }
                            .addOnFailureListener { exception ->
                                Log.e("TransactionViewModel", "Error deleting document: ", exception)
                            }
                    }
                }
        }
    }

    fun updateTransaction(updatedTransaction: Transaction) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            db.collection("transactions")
                .whereEqualTo("userID", userId)
                .whereEqualTo("transactionID", updatedTransaction.transactionID)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        db.collection("transactions").document(document.id)
                            .set(updatedTransaction)
                            .addOnSuccessListener {
                                loadTransactionsFromFirestore()
                            }
                            .addOnFailureListener { exception ->
                                Log.e("TransactionViewModel", "Error updating document: ", exception)
                            }
                    }
                }
        }
    }

    fun updateTransactionsForDeletedCategory(categoryId: Int, isIncome: Boolean) {
        val defaultCategoryId = if (isIncome) 10 else 14
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            db.collection("transactions")
                .whereEqualTo("userID", userId)
                .whereEqualTo("categoryID", categoryId)
                .get()
                .addOnSuccessListener { documents ->
                    val batch = db.batch()
                    for (document in documents) {
                        val transaction = document.toObject(Transaction::class.java)
                        transaction.categoryID = defaultCategoryId
                        val transactionRef = db.collection("transactions").document(document.id)
                        batch.set(transactionRef, transaction)
                    }
                    batch.commit()
                        .addOnSuccessListener {
                            loadTransactionsFromFirestore()
                        }
                        .addOnFailureListener { exception ->
                            Log.e("TransactionViewModel", "Error updating transactions: ", exception)
                        }
                }
                .addOnFailureListener { exception ->
                    Log.e("TransactionViewModel", "Error getting transactions: ", exception)
                }
        }
    }
}
