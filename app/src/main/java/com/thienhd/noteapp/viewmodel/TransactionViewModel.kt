package com.thienhd.noteapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thienhd.noteapp.data.entities.Transaction
import com.thienhd.noteapp.data.DateTransaction
import com.thienhd.noteapp.data.FilterCriteria
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val categoryViewModel = CategoryViewModel.getInstance(application)

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private val _dateTransactions = MutableLiveData<List<DateTransaction>>()
    val dateTransactions: LiveData<List<DateTransaction>> get() = _dateTransactions

    private var originalTransactions: List<Transaction> = listOf()

    private val _currentFilterCriteria = MutableLiveData<FilterCriteria>()
    val currentFilterCriteria: LiveData<FilterCriteria> get() = _currentFilterCriteria
    init {
        viewModelScope.launch {
            loadTransactionsFromFirestore()
        }
    }

    fun loadTransactionsFromFirestore() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("transactions")
            .whereEqualTo("userID", userId)
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val transactionsList = documents.map { document ->
                    val transaction = document.toObject(Transaction::class.java)
                    transaction.transactionID = document.id // Store document ID as transactionID
                    transaction
                }
                originalTransactions = transactionsList
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
                "hour" to transaction.hour,
            )

            db.collection("transactions")
                .add(transactionData)
                .addOnSuccessListener { documentReference ->
                    transaction.transactionID = documentReference.id // Store document ID as transactionID
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
            db.collection("transactions").document(id)
                .delete()
                .addOnSuccessListener {
                    loadTransactionsFromFirestore()
                }
                .addOnFailureListener { exception ->
                    Log.e("TransactionViewModel", "Error deleting document: ", exception)
                }
        }
    }

    fun updateTransaction(updatedTransaction: Transaction) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            db.collection("transactions").document(updatedTransaction.transactionID)
                    .set(updatedTransaction)
                    .addOnSuccessListener {
                        loadTransactionsFromFirestore()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("TransactionViewModel", "Error updating document: ", exception)
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
                        transaction.transactionID = document.id // Ensure document ID is set
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

    fun filterTransactions(criteria: FilterCriteria) {
        var filteredTransactions = originalTransactions.toMutableList()
        _currentFilterCriteria.value = criteria

        criteria.startDate?.let { startDate ->
            filteredTransactions = filteredTransactions.filter { it.date.toDate().after(startDate) }.toMutableList()
        }

        criteria.endDate?.let { endDate ->
            filteredTransactions = filteredTransactions.filter { it.date.toDate().before(endDate) }.toMutableList()
        }

        criteria.type?.let { type ->
            filteredTransactions = when (type) {
                1 -> filteredTransactions.filter { it.type == 1 }.toMutableList() // Income
                2 -> filteredTransactions.filter { it.type == 0 }.toMutableList() // Expense
                else -> filteredTransactions
            }
        }

        criteria.order?.let { order ->
            filteredTransactions = when (order) {
                1 -> filteredTransactions.sortedBy { it.date }.toMutableList()
                else -> filteredTransactions.sortedByDescending { it.date }.toMutableList()
            }
        }

        _transactions.value = filteredTransactions
    }

    fun resetTransactions() {
        _transactions.value = originalTransactions
        _currentFilterCriteria.value = FilterCriteria(null,null,0,0)
    }

    fun searchTransactions(query: String) {
        val filteredTransactions = originalTransactions.filter {
            it.note.contains(query, ignoreCase = true) ||
                    categoryViewModel.getCategoryById(it.categoryID)?.name?.contains(query, ignoreCase = true) == true
        }
        _transactions.value = filteredTransactions.toMutableList()
    }
}
