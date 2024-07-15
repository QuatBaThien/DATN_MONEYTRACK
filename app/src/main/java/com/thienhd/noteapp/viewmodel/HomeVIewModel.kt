package com.thienhd.noteapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.thienhd.noteapp.data.entities.Transaction
import com.thienhd.noteapp.data.entities.Wallet
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val walletViewModel = WalletViewModel.getInstance(application)
    private val transactionViewModel = TransactionViewModel.getInstance(application)
    private val categoryViewModel = CategoryViewModel.getInstance(application)

    private val _walletSum = MutableLiveData<Double>()
    val walletSum: LiveData<Double> get() = _walletSum

    private val _wallet = MutableLiveData<Wallet>()
    val wallet: LiveData<Wallet> get() = _wallet

    private val _weekExpense = MutableLiveData<Pair<Double, Double>>() // Pair of last week and this week expenses
    val weekExpense: LiveData<Pair<Double, Double>> get() = _weekExpense

    private val _monthExpense = MutableLiveData<Pair<Double, Double>>() // Pair of last month and this month expenses
    val monthExpense: LiveData<Pair<Double, Double>> get() = _monthExpense

    private val _recentTransactions = MutableLiveData<List<Transaction>>()
    val recentTransactions: LiveData<List<Transaction>> get() = _recentTransactions

    private val _weekTopCategories = MutableLiveData<List<Triple<Int, Double, Double>>>()
    val weekTopCategories: LiveData<List<Triple<Int, Double, Double>>> get() = _weekTopCategories

    private val _monthTopCategories = MutableLiveData<List<Triple<Int, Double, Double>>>()
    val monthTopCategories: LiveData<List<Triple<Int, Double, Double>>> get() = _monthTopCategories


    init {
        loadWalletSum()
        loadHighestBalanceWallet()
        loadRecentTransactions()
        loadExpenseReport()
    }

    fun loadWalletSum() {
        walletViewModel.wallets.observeForever { wallets ->
            val sum = wallets.filter { !it.isDeleted }.sumOf { it.balance }
            _walletSum.postValue(sum)
        }
    }

    fun loadHighestBalanceWallet() {
        walletViewModel.wallets.observeForever { wallets ->
            _wallet.postValue(wallets.filter { !it.isDeleted }.maxByOrNull { it.balance })
        }
    }

    fun loadRecentTransactions() {
        transactionViewModel.transactions.observeForever { transactions ->
            val recentTransactions = transactions.take(3)
            _recentTransactions.postValue(recentTransactions)
        }
    }

    fun loadExpenseReport() {
        transactionViewModel.transactions.observeForever { transactions ->
            val now = Calendar.getInstance()
            val currentWeek = now.get(Calendar.WEEK_OF_YEAR)
            val currentMonth = now.get(Calendar.MONTH)
            val currentYear = now.get(Calendar.YEAR)

            val lastWeekExpenses = transactions.filter {
                val cal = Calendar.getInstance().apply { time = it.date.toDate() }
                cal.get(Calendar.WEEK_OF_YEAR) == currentWeek - 1 && cal.get(Calendar.YEAR) == currentYear && (it.type == 1 || it.type == 4 || it.type == 5)
            }.sumOf { it.amount }

            val thisWeekExpenses = transactions.filter {
                val cal = Calendar.getInstance().apply { time = it.date.toDate() }
                cal.get(Calendar.WEEK_OF_YEAR) == currentWeek && cal.get(Calendar.YEAR) == currentYear && (it.type == 1 || it.type == 4 || it.type == 5)
            }.sumOf { it.amount }

             _weekExpense.postValue(Pair(lastWeekExpenses, thisWeekExpenses))

            val lastMonthExpenses = transactions.filter {
                val cal = Calendar.getInstance().apply { time = it.date.toDate() }
                cal.get(Calendar.MONTH) == currentMonth - 1 && cal.get(Calendar.YEAR) == currentYear && (it.type == 1 || it.type == 4 || it.type == 5)
            }.sumOf { it.amount }

            val thisMonthExpenses = transactions.filter {
                val cal = Calendar.getInstance().apply { time = it.date.toDate() }
                cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear && (it.type == 1 || it.type == 4 || it.type == 5)
            }.sumOf { it.amount }

            _monthExpense.postValue(Pair(lastMonthExpenses, thisMonthExpenses))

            // Danh mục chi tiêu hàng đầu cho tuần
            val weekCategoryExpenseMap = transactions.filter {
                val cal = Calendar.getInstance().apply { time = it.date.toDate() }
                cal.get(Calendar.WEEK_OF_YEAR) == currentWeek && cal.get(Calendar.YEAR) == currentYear && (it.type == 1 || it.type == 4 || it.type == 5)
            }.groupBy { it.categoryID }.mapValues {
                it.value.sumOf { transaction -> transaction.amount }
            }

            val topWeekCategories = weekCategoryExpenseMap.toList().sortedByDescending { (_, value) -> value }.take(3)
            _weekTopCategories.postValue(topWeekCategories.map {
                Triple(it.first, it.second, thisWeekExpenses)
            })

            // Danh mục chi tiêu hàng đầu cho tháng
            val monthCategoryExpenseMap = transactions.filter { it.type == 1 || it.type == 4 || it.type == 5 &&
                    it.date.toDate().let { date ->
                        Calendar.getInstance().apply { time = date }.get(Calendar.MONTH) == currentMonth
                    }
            }.groupBy { it.categoryID }.mapValues {
                it.value.sumOf { transaction -> transaction.amount }
            }

            val topMonthCategories = monthCategoryExpenseMap.toList().sortedByDescending { (_, value) -> value }.take(3)
            _monthTopCategories.postValue(topMonthCategories.map {
                Triple(it.first, it.second, thisMonthExpenses)
            })
        }
    }

    fun getExpenseChangePercent(lastPeriod: Double, currentPeriod: Double): Int {
        return if (lastPeriod == 0.0) {
            if (currentPeriod == 0.0) 0 else 100
        } else {
            (((currentPeriod - lastPeriod) / lastPeriod) * 100).roundToInt()
        }
    }
}
