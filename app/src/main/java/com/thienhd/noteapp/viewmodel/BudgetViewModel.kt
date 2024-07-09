package com.thienhd.noteapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.thienhd.noteapp.data.entities.Budget
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance().apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }
    private val categoryViewModel = CategoryViewModel.getInstance(application)
    private lateinit var transactionViewModel: TransactionViewModel

    private val _budgets = MutableLiveData<List<Budget>>()
    val budgets: LiveData<List<Budget>> get() = _budgets

    private val _filteredBudgets = MutableLiveData<List<Budget>>()
    val filteredBudgets: LiveData<List<Budget>> get() = _filteredBudgets

    private val budgetDocumentIds = mutableMapOf<String, String>()

    init {
        loadBudgetsFromFirestore()
    }

    fun setTransactionViewModel(transactionViewModel: TransactionViewModel) {
        this.transactionViewModel = transactionViewModel
        transactionViewModel.transactions.observeForever { transactions ->
            calculateCurrentAmounts()
        }
    }

    private fun loadBudgetsFromFirestore() {
        val userId = auth.currentUser?.uid ?: return
        Log.d("BudgetViewModel", "UserID: $userId")

        db.collection("budgets")
            .whereEqualTo("userID", userId)
            .get()
            .addOnSuccessListener { documents ->
                val budgetList = mutableListOf<Budget>()
                for (document in documents) {
                    val budget = document.toObject(Budget::class.java)
                    val documentId = document.id
                    budget.budgetID = documentId
                    budgetDocumentIds[budget.budgetID] = documentId
                    budgetList.add(budget)
                }
                _budgets.postValue(budgetList)
            }
            .addOnFailureListener { exception ->
                Log.e("BudgetViewModel", "Error getting documents: ", exception)
            }
    }

    private fun calculateCurrentAmounts() {
        val transactions = transactionViewModel.transactions.value ?: return
        val budgets = _budgets.value ?: return

        for (budget in budgets) {
            val currentAmount = transactions.filter { transaction ->
                transaction.categoryID == budget.categoryID &&
                        transaction.date.toDate().after(budget.dateStart) &&
                        transaction.date.toDate().before(budget.dateEnd)
            }.sumOf { it.amount }

            budget.currentAmount = currentAmount
        }
        _budgets.postValue(budgets)
    }

    private fun calculateCurrentAmount(budget: Budget) {
        val transactions = transactionViewModel.transactions.value ?: return
        val currentAmount = transactions.filter { transaction ->
            transaction.categoryID == budget.categoryID &&
                    transaction.date.toDate().after(budget.dateStart) &&
                    transaction.date.toDate().before(budget.dateEnd)
        }.sumOf { it.amount }
        budget.currentAmount = currentAmount
    }

    fun addBudget(budget: Budget) {
        calculateCurrentAmount(budget)
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val budgetData = hashMapOf(
                "categoryID" to budget.categoryID,
                "targetAmount" to budget.targetAmount,
                "currentAmount" to budget.currentAmount,
                "dateStart" to budget.dateStart,
                "dateEnd" to budget.dateEnd,
                "userID" to userId
            )

            db.collection("budgets").add(budgetData)
                .addOnSuccessListener {
                    loadBudgetsFromFirestore()
                }
                .addOnFailureListener { exception ->
                    Log.e("BudgetViewModel", "Error adding document: ", exception)
                }
        }
    }

    fun updateBudget(updatedBudget: Budget) {
        viewModelScope.launch {
            val documentId = budgetDocumentIds[updatedBudget.budgetID] ?: return@launch
            calculateCurrentAmount(updatedBudget)
            db.collection("budgets").document(documentId)
                .set(updatedBudget)
                .addOnSuccessListener {
                    Log.d("BudgetViewModel", "Budget updated successfully")
                    loadBudgetsFromFirestore()
                }
                .addOnFailureListener { exception ->
                    Log.e("BudgetViewModel", "Error updating document: ", exception)
                }
        }
    }

    fun deleteBudget(budgetId: String) {
        viewModelScope.launch {
            val documentId = budgetDocumentIds[budgetId] ?: return@launch
            db.collection("budgets").document(documentId)
                .delete()
                .addOnSuccessListener {
                    Log.d("BudgetViewModel", "Budget deleted successfully")
                    loadBudgetsFromFirestore()
                }
                .addOnFailureListener { exception ->
                    Log.e("BudgetViewModel", "Error deleting document: ", exception)
                }
        }
    }

    fun getBudgetById(budgetId: String): Budget? {
        return _budgets.value?.find { it.budgetID == budgetId }
    }

    fun filterBudgetsByType(type: Int) {
        val filteredBudgets = _budgets.value?.filter { budget ->
            val category = categoryViewModel.getCategoryById(budget.categoryID)
            category?.type == type
        } ?: emptyList()
        _filteredBudgets.postValue(filteredBudgets)
    }
}
