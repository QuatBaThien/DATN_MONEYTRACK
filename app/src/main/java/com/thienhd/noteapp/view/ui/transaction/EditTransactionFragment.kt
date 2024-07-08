package com.thienhd.noteapp.view.ui.transaction

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.thienhd.noteapp.R
import com.thienhd.noteapp.data.entities.Transaction
import com.thienhd.noteapp.databinding.FragmentEditTransactionBinding
import com.thienhd.noteapp.viewmodel.CategoryViewModel
import com.thienhd.noteapp.viewmodel.ChooseCategoryViewModel
import com.thienhd.noteapp.viewmodel.ChooseWalletViewModel
import com.thienhd.noteapp.viewmodel.TransactionViewModel
import com.thienhd.noteapp.viewmodel.WalletViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class EditTransactionFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private var _binding: FragmentEditTransactionBinding? = null
    private val binding get() = _binding!!
    private val categoryViewModel: CategoryViewModel by activityViewModels()
    private val walletViewModel: WalletViewModel by activityViewModels()
    private val transactionViewModel: TransactionViewModel by activityViewModels()
    private val chooseCategoryViewModel: ChooseCategoryViewModel by activityViewModels()
    private val chooseWalletViewModel: ChooseWalletViewModel by activityViewModels()
    private var isCategorySelected = false
    private var isWalletSelected = false
    private var isWalletAmount = false
    private var isReset = true
    private val calendar: Calendar = Calendar.getInstance()
    private var transactionId: String? = null
    private var currentTransaction: Transaction? = null

    private var initialWalletID: String? = null
    private var initialCategoryID: Int? = null
    private var initialNote: String? = null
    private var initialDate: String? = null
    private var initialAmount: Double? = null

    private var isWalletIDChange = false
    private var isCategoryIDChange = false
    private var isNoteChange= false
    private var isDateChange= false
    private var isAmountChange= false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionId = arguments?.getString("transactionId")
        Log.d("transactionId", " ${transactionId} ")
        transactionId?.let {
            currentTransaction = transactionViewModel.getTransactionById(it)
            currentTransaction?.let { transaction ->
                initialAmount = transaction.amount
                initialNote = transaction.note
                initialCategoryID = transaction.categoryID
                initialWalletID = transaction.walletID
                populateTransactionDetails(transaction)
            }
        }

        chooseCategoryViewModel.categoryId.observe(viewLifecycleOwner) { categoryId ->
            if (categoryId != null) {
                updateCategoryUI(categoryId)
                isCategorySelected = true
                isCategoryIDChange = categoryId != initialCategoryID
            } else {
                isCategorySelected = false
            }

            updateSaveButtonState()
        }

        chooseWalletViewModel.walletId.observe(viewLifecycleOwner) { walletId ->
            if (walletId != null) {
                updateWalletUI(walletId)
                isWalletSelected = true
                isWalletIDChange = walletId != initialWalletID
            } else {
                isWalletSelected = false
            }

            updateSaveButtonState()
        }

        // Add text watcher to update save button state when amount is changed
        binding.etTransactionAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isAmountChange = s.toString() != initialAmount.toString()
                updateSaveButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.tvTranContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isNoteChange = s.toString() != initialNote
                updateSaveButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.chooseCategory.setOnClickListener {
            isReset = false
            findNavController().navigate(R.id.action_editTransactionFragment_to_chooseCategoryFragment)

        }

        binding.chooseWallet.setOnClickListener {
            isReset = false
            findNavController().navigate(R.id.action_editTransactionFragment_to_chooseWalletFragment)

        }

        binding.btBack.setOnClickListener {
            findNavController().navigate(R.id.action_editTransactionFragment_to_TransactionFragment)
        }

        binding.btTrash.setOnClickListener {
            transactionId?.let { id ->
                transactionViewModel.deleteTransaction(id)
                findNavController().navigateUp()
            }
        }

        binding.tvTransactionTime.setOnClickListener {
            showDateTimePicker()
        }



        binding.btSaveTransaction.setOnClickListener {
            saveTransaction()
        }

        updateSaveButtonState()
    }

    private fun populateTransactionDetails(transaction: Transaction) {
        Log.d("transactionId", " trans ${transaction.transactionID} ")
        binding.etTransactionAmount.setText(transaction.amount.toString())
        binding.tvTranContent.setText(transaction.note)
        updateCategoryUI(transaction.categoryID)
        updateWalletUI(transaction.walletID)
        updateDateTimeDisplay(transaction.date.toDate(), transaction.hour)
    }

    private fun updateCategoryUI(categoryId: Int) {
        val category = categoryViewModel.getCategoryById(categoryId)
        Log.d("transactionId", " cate ${category?.name} ")
        category?.let {
            binding.ivTransactionIcon.setImageResource(getIconResource(it.iconId))
            binding.tvTransactionTitle.text = it.name
        }
    }

    private fun updateWalletUI(walletId: String) {
        val wallet = walletViewModel.getWalletByWalletID(walletId)
        Log.d("transactionId", " cate ${wallet?.name} ")
        wallet?.let {
            binding.tvWalletTitle.text = it.name
            binding.tvWalletBalance.text = it.balance
        }
    }

    private fun resetCategoryUI() {
        binding.ivTransactionIcon.setImageResource(R.drawable.ic_category)
        binding.tvTransactionTitle.text = getString(R.string.default_category_text)
    }

    private fun resetWalletUI() {
        binding.tvWalletTitle.text = getString(R.string.default_wallet_text)
        binding.tvWalletBalance.text = ""
    }

    private fun updateSaveButtonState() {
        val hasChanges = isAmountChange || isWalletIDChange || isCategoryIDChange || isNoteChange || binding.tvTransactionTime.text.toString() != initialDate
        binding.btSaveTransaction.isEnabled = hasChanges
        binding.btSaveTransaction.backgroundTintList = if (hasChanges) {
            ContextCompat.getColorStateList(requireContext(), R.color.blue)
        } else {
            ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
        }
    }

    private fun getIconResource(iconId: Int): Int {
        val iconName = "ic_item_$iconId"
        return resources.getIdentifier(iconName, "drawable", requireContext().packageName)
    }

    private fun showDateTimePicker() {
        DatePickerDialog(
            requireContext(),
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        TimePickerDialog(
            requireContext(),
            this,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        updateDateTimeDisplay(calendar)
    }

    private fun updateDateTimeDisplay(calendar: Calendar) {
        val sdf = SimpleDateFormat("HH:mm, EEEE, dd MMMM yyyy", Locale("vi", "VN"))
        binding.tvTransactionTime.text = sdf.format(calendar.time)
        updateSaveButtonState()
    }

    private fun updateDateTimeDisplay(date: Date, time: String) {
        val sdf = SimpleDateFormat("HH:mm, EEEE, dd MMMM yyyy", Locale("vi", "VN"))
        binding.tvTransactionTime.text = sdf.format(date)
        initialDate = sdf.format(date)
    }

    private fun saveTransaction() {
        val amount = binding.etTransactionAmount.text.toString()
        val note = binding.tvTranContent.text.toString()
        val categoryId = currentTransaction?.categoryID ?: 0
        val walletId = currentTransaction?.walletID ?: ""
        val type = currentTransaction?.type ?: 0

        val wallet = walletViewModel.getWalletByWalletID(walletId)
        wallet?.let {
            val currentBalance = it.balance.toDoubleOrNull() ?: 0.0
            val transactionAmount = amount.toDoubleOrNull() ?: 0.0

            if (type == 1 && transactionAmount > currentBalance) {
                // Expense and insufficient balance
                showToast("Giao dịch thất bại: Số dư không đủ.")
                return
            }

            val newBalance = if (type == 1) {
                currentBalance - transactionAmount
            } else {
                currentBalance + transactionAmount
            }

            walletViewModel.updateWalletBalance(walletId, newBalance)
            val transaction = Transaction(
                transactionID = currentTransaction?.transactionID ?: "",
                walletID = walletId,
                categoryID = categoryId,
                note = note,
                type = type,
                amount = amount.toDoubleOrNull()?: 0.0,
                date = Timestamp(calendar.time), // Save the updated timestamp
                hour = SimpleDateFormat("HH:mm", Locale("vi", "VN")).format(calendar.time),
                userID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            )

            transactionViewModel.updateTransaction(transaction)
        }
        findNavController().navigateUp()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        if (isReset) {
            chooseCategoryViewModel.resetCategory()
            chooseWalletViewModel.resetWallet()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (isReset) {
            chooseCategoryViewModel.resetCategory()
            chooseWalletViewModel.resetWallet()
        }
    }
}
