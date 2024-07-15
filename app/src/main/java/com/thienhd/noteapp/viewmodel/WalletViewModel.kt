package com.thienhd.noteapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thienhd.noteapp.data.entities.Wallet
import kotlinx.coroutines.launch

class WalletViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _wallets = MutableLiveData<List<Wallet>>()
    val wallets: LiveData<List<Wallet>> get() = _wallets

    init {
        // Initially load wallets from Firestore
        viewModelScope.launch {
            loadWalletsFromFirestore()
        }
    }
    companion object {
        @Volatile private var instance: WalletViewModel? = null

        fun getInstance(application: Application): WalletViewModel {
            return instance ?: synchronized(this) {
                instance ?: WalletViewModel(application).also { instance = it }
            }
        }
    }
    fun loadWalletsFromFirestore() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("wallets")
            .whereEqualTo("userID", userId)
            .get()
            .addOnSuccessListener { documents ->
                val walletsList = documents.map { document ->
                    val wallet = document.toObject(Wallet::class.java)
                    wallet.walletID = document.id // Store document ID
                    wallet
                }
                _wallets.postValue(walletsList)
            }
            .addOnFailureListener { exception ->
                Log.e("WalletViewModel", "Error getting documents: ", exception)
            }
    }

    fun addWallet(wallet: Wallet) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val walletData = hashMapOf(
                "balance" to wallet.balance,
                "isDeleted" to wallet.isDeleted,
                "name" to wallet.name,
                "userID" to userId
            )

            db.collection("wallets").add(walletData)
                .addOnSuccessListener { documentReference ->
                    wallet.walletID = documentReference.id // Store document ID in wallet object
                    loadWalletsFromFirestore()
                }
                .addOnFailureListener { exception ->
                    Log.e("WalletViewModel", "Error adding document: ", exception)
                }
        }
    }

    fun deleteWallet(walletID: String) {
        viewModelScope.launch {
            db.collection("wallets").document(walletID)
                .delete()
                .addOnSuccessListener {
                    loadWalletsFromFirestore()
                }
                .addOnFailureListener { exception ->
                    Log.e("WalletViewModel", "Error deleting document: ", exception)
                }
        }
    }

    fun updateWallet(walletID: String, updatedWallet: Wallet) {
        viewModelScope.launch {
            db.collection("wallets").document(walletID)
                .set(updatedWallet)
                .addOnSuccessListener {
                    loadWalletsFromFirestore()
                }
                .addOnFailureListener { exception ->
                    Log.e("WalletViewModel", "Error updating document: ", exception)
                }
        }
    }

    fun updateWalletBalance(walletID: String, newBalance: Double) {
        viewModelScope.launch {
            val walletRef = db.collection("wallets").document(walletID)
            walletRef.update("balance", newBalance)
                .addOnSuccessListener {
                    loadWalletsFromFirestore()
                }
                .addOnFailureListener { exception ->
                    Log.e("WalletViewModel", "Error updating wallet balance: ", exception)
                }
        }
    }

    fun getWalletByWalletID(walletID: String): Wallet? {
        return _wallets.value?.find { it.walletID == walletID }
    }
}
