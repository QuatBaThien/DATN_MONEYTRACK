package com.thienhd.noteapp.view.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thienhd.noteapp.R
import com.thienhd.noteapp.data.entities.Category
import com.thienhd.noteapp.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: FragmentMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHost = childFragmentManager.findFragmentById(R.id.main_container)
        val navView: BottomNavigationView = binding.bottomAppBar
        navHost?.findNavController()?.let {
            navView.setupWithNavController(it) }
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_createTransactionFragment)
        }
        checkFirstLogin()
    }
    private fun checkFirstLogin() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("categories")
            .whereEqualTo("userID", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No categories exist for this user, add default categories
                    addDefaultCategories(userId)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Lỗi kiểm tra danh mục: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        db.collection("wallets")
            .whereEqualTo("userID", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No wallet exists for this user, show create wallet dialog
                    showCreateWalletDialog()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Lỗi kiểm tra ví tiền: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun addDefaultCategories(userId: String) {
        val categories = listOf(
            Category(1,3, "Mua sắm", 1, "userID"), // iconId, name, type, userID
            Category(2,4, "Ăn uống", 1, "userID"),
        Category(3, 8, "Chi tiêu hàng ngày", 1, "userID"),
        Category(4, 9, "Đi lại", 1, "userID"),
        Category(5, 6, "Hóa đơn", 1, "userID"),
        Category(6,19, "Mỹ phẩm", 1, "userID"),
        Category(7,17, "Y tế", 1, "userID"),
        Category(8,1, "Tiền thưởng", 0, "userID"),
        Category(9,2, "Tiền lương", 0, "userID"),
        Category(10,18, "Thu nhập khác", 0, "userID"),
        Category(11,16, "Đầu tư", 0, "userID"),
        Category(12,20, "Đi vay", 0, "userID"),
        Category(13,12, "Thu nợ", 0, "userID"),
        Category(14,18, "Khoản chi khác", 1, "userID")
        )

        val batch = db.batch()

        categories.forEach { category ->
            val categoryRef = db.collection("categories").document()
            val categoryData = hashMapOf(
                "categoryID" to category.categoryID,
                "userID" to userId,
                "name" to category.name,
                "iconId" to category.iconId,
                "type" to category.type
            )
            batch.set(categoryRef, categoryData)
        }

        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Thêm danh mục mặc định thành công", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Thêm danh mục thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showCreateWalletDialog() {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_create_frist_wallet, null)

        val walletNameEditText = dialogView.findViewById<EditText>(R.id.editTextWalletName)
        val walletBalanceEditText = dialogView.findViewById<EditText>(R.id.editTextWalletBalance)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<View>(R.id.buttonCreateWallet).setOnClickListener {
            val walletName = walletNameEditText.text.toString()
            val walletBalance = walletBalanceEditText.text.toString().toDoubleOrNull() ?: 0.0

            if (walletName.isNotEmpty()) {
                createWallet(walletName, walletBalance.toString())
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập tên ví", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun createWallet(walletName: String, walletBalance: String) {
        val userId = auth.currentUser?.uid ?: return
        val wallet = hashMapOf(
            "userID" to userId,
            "name" to walletName,
            "balance" to walletBalance,
            "isDelete" to false
        )

        db.collection("wallets").document("wallet_${userId}_1")
            .set(wallet)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Tạo ví thành công", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Tạo ví thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
