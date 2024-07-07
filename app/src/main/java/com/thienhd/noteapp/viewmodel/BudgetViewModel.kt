package com.thienhd.noteapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thienhd.noteapp.data.entities.Budget
import com.thienhd.noteapp.data.entities.Transaction
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var transactionViewModel: TransactionViewModel

    private val _budgets = MutableLiveData<List<Budget>>()
    val budgets: LiveData<List<Budget>> get() = _budgets

    private val _filteredBudgets = MutableLiveData<List<Budget>>().apply { value = emptyList() }
    val filteredBudgets: LiveData<List<Budget>> get() = _filteredBudgets

    init {
        loadBudgetsFromFirestore()
    }

    fun setCategoryViewModel(categoryViewModel: CategoryViewModel) {
        this.categoryViewModel = categoryViewModel
    }

    fun setTransactionViewModel(transactionViewModel: TransactionViewModel) {
        this.transactionViewModel = transactionViewModel
    }

    fun loadBudgetsFromFirestore() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("budgets")
            .whereEqualTo("userID", userId)
            .get()
            .addOnSuccessListener { documents ->
                val budgetList = documents.map { document ->
                    val budget = document.toObject(Budget::class.java)
                    budget.budgetID = document.id // Store document ID
                    budget
                }
                calculateCurrentAmounts(budgetList)
                _budgets.postValue(budgetList)
            }
            .addOnFailureListener { exception ->
                Log.e("BudgetViewModel", "Error getting documents: ", exception)
            }
    }

    private fun calculateCurrentAmounts(budgetList: List<Budget>) {
        val transactions = transactionViewModel.transactions.value ?: return

        for (budget in budgetList) {
            val currentAmount = transactions.filter { it.categoryID == budget.categoryID }
                .sumOf { it.amount.toDoubleOrNull() ?: 0.0 }

            budget.currentAmount = currentAmount
        }
    }

    fun addBudget(budget: Budget) {
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

    fun filterBudgetsByType(type: Int) {
        val filteredBudgets = _budgets.value?.filter { budget ->
            val category = categoryViewModel.getCategoryById(budget.categoryID)
            category?.type == type
        } ?: emptyList()
        _filteredBudgets.postValue(filteredBudgets)
    }
}
