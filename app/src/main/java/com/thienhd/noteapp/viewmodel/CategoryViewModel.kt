package com.thienhd.noteapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.thienhd.noteapp.data.entities.Category
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance().apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    private val _expenseCategories = MutableLiveData<List<Category>>()
    val expenseCategories: LiveData<List<Category>> get() = _expenseCategories

    private val _incomeCategories = MutableLiveData<List<Category>>()
    val incomeCategories: LiveData<List<Category>> get() = _incomeCategories

    private val _selectedIcon = MutableLiveData<Int?>()
    val selectedIcon: LiveData<Int?> get() = _selectedIcon

    private val categoryDocumentIds = mutableMapOf<Int, String>()

    init {
        // Initially load categories from Firestore
        viewModelScope.launch {
            loadCategoriesFromFirestore()
        }
    }
    companion object {
        @Volatile private var instance: CategoryViewModel? = null

        fun getInstance(application: Application): CategoryViewModel {
            return instance ?: synchronized(this) {
                instance ?: CategoryViewModel(application).also { instance = it }
            }
        }
    }

    fun loadCategoriesFromFirestore() {
        val userId = auth.currentUser?.uid ?: return
        Log.d("CategoryViewModel", "UserID: $userId")

        db.collection("categories")
            .whereEqualTo("userID", userId)
            .get()
            .addOnSuccessListener { documents ->
                val expenses = mutableListOf<Category>()
                val incomes = mutableListOf<Category>()
                for (document in documents) {
                    val category = document.toObject(Category::class.java)
                    val documentId = document.id
                    categoryDocumentIds[category.categoryID] = documentId // Store the documentId using the categoryID as key
                    Log.d("CategoryViewModel", "Category: ${category.name}, Type: ${category.type}, iconId: ${category.iconId}, documentId: $documentId")
                    if (category.type == 1) {
                        expenses.add(category)
                    } else {
                        incomes.add(category)
                    }
                }
                _expenseCategories.postValue(expenses)
                _incomeCategories.postValue(incomes)
                Log.d("CategoryViewModel", "Expenses: ${expenses.size}, Incomes: ${incomes.size}")
            }
            .addOnFailureListener { exception ->
                Log.e("CategoryViewModel", "Error getting documents: ", exception)
            }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val categoryData = hashMapOf(
                "categoryID" to category.categoryID,
                "iconId" to category.iconId,
                "name" to category.name,
                "type" to category.type,
                "userID" to userId
            )

            db.collection("categories").add(categoryData)
                .addOnSuccessListener {
                    Log.d("CategoryViewModel", "Category added successfully")
                    loadCategoriesFromFirestore()
                }
                .addOnFailureListener { exception ->
                    Log.e("CategoryViewModel", "Error adding document: ", exception)
                }
        }
    }

    fun getCategoryById(id: Int): Category? {
        return _expenseCategories.value?.find { it.categoryID == id }
            ?: _incomeCategories.value?.find { it.categoryID == id }
    }

    fun updateCategory(updatedCategory: Category) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val documentId = categoryDocumentIds[updatedCategory.categoryID] ?: return@launch
            db.collection("categories").document(documentId)
                .set(updatedCategory)
                .addOnSuccessListener {
                    Log.d("CategoryViewModel", "Category updated successfully")
                    loadCategoriesFromFirestore()
                }
                .addOnFailureListener { exception ->
                    Log.e("CategoryViewModel", "Error updating document: ", exception)
                }
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val documentId = categoryDocumentIds[id] ?: return@launch
            db.collection("categories").document(documentId)
                .delete()
                .addOnSuccessListener {
                    Log.d("CategoryViewModel", "Category deleted successfully")
                    loadCategoriesFromFirestore()
                }
                .addOnFailureListener { exception ->
                    Log.e("CategoryViewModel", "Error deleting document: ", exception)
                }
        }
    }

    fun selectIcon(iconIndex: Int) {
        _selectedIcon.value = iconIndex
    }

    fun clearSelectedIcon() {
        _selectedIcon.value = null
    }
}