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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.thienhd.noteapp.R
import com.thienhd.noteapp.data.entities.Transaction
import com.thienhd.noteapp.data.entities.Wallet
import com.thienhd.noteapp.databinding.FragmentEditTransactionBinding
import com.thienhd.noteapp.viewmodel.*
import java.text.SimpleDateFormat
import java.util.*

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
    private var isNoteChange = false
    private var isDateChange = false
    private var isAmountChange = false

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
        Log.d("transactionId", " $transactionId ")

        transactionId?.let {
            currentTransaction = transactionViewModel.getTransactionById(it)
            currentTransaction?.let { transaction ->
                when (transaction.type) {
                    3 -> setupDebtTransaction(transaction)
                    4 -> setupLoanTransaction(transaction)
                    5 -> setupPayDebtTransaction(transaction)
                    6 -> setupGetLoanTransaction(transaction)
                    else -> setupRegularTransaction(transaction)
                }
            }
        }

        observeViewModels()
        setupListeners()
        updateSaveButtonState()
    }

    private fun setupDebtTransaction(transaction: Transaction) {
        populateTransactionDetails(transaction)
        setTransactionUI(R.drawable.ic_item_debt, "Vay tiền", "Thông tin giao dịch vay")
        disableUserInteractions()
        hideTrashButton()
    }

    private fun setupLoanTransaction(transaction: Transaction) {
        populateTransactionDetails(transaction)
        setTransactionUI(R.drawable.ic_item_loan, "Cho vay", "Thông tin giao dịch cho vay")
        disableUserInteractions()
        hideTrashButton()
    }
    private fun setupPayDebtTransaction(transaction: Transaction) {
        populateTransactionDetails(transaction)
        setTransactionUI(R.drawable.ic_item_paid_debt, "Trả nợ", "Thông tin giao dịch cho vay")
        disableUserInteractions()
        hideTrashButton()
    }

    private fun setupGetLoanTransaction(transaction: Transaction) {
        populateTransactionDetails(transaction)
        setTransactionUI(R.drawable.ic_item_get_paid, "Thu nợ", "Thông tin giao dịch cho vay")
        disableUserInteractions()
        hideTrashButton()
    }

    private fun setupRegularTransaction(transaction: Transaction) {
        initialAmount = transaction.amount
        initialNote = transaction.note
        initialCategoryID = transaction.categoryID
        initialWalletID = transaction.walletID
        populateTransactionDetails(transaction)
    }

    private fun observeViewModels() {
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
    }

    private fun setupListeners() {
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
        category?.let {
            binding.ivTransactionIcon.setImageResource(getIconResource(it.iconId))
            binding.tvTransactionTitle.text = it.name
        }
    }

    private fun updateWalletUI(walletId: String) {
        val wallet = walletViewModel.getWalletByWalletID(walletId)
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
        val newAmountStr = binding.etTransactionAmount.text.toString()
        val note = binding.tvTranContent.text.toString()
        val newCategoryId = chooseCategoryViewModel.categoryId.value ?: currentTransaction?.categoryID ?: 0
        val newWalletId = chooseWalletViewModel.walletId.value ?: currentTransaction?.walletID ?: ""
        val newAmount = newAmountStr.toDoubleOrNull() ?: 0.0
        val type = currentTransaction?.type ?: 0

        val oldAmount = currentTransaction?.amount ?: 0.0
        val newWallet = walletViewModel.getWalletByWalletID(newWalletId)
        val oldWallet = initialWalletID?.let { walletViewModel.getWalletByWalletID(it) }

        if (newWallet != null && oldWallet != null) {
            handleWalletBalances(newWallet, oldWallet, newAmount, oldAmount, newWalletId, type)
            val transaction = createTransaction(newWalletId, newCategoryId, note, newAmount, type)
            transactionViewModel.updateTransaction(transaction)
            findNavController().navigateUp()
        } else {
            showToast("Lỗi: Không tìm thấy ví.")
        }
    }

    private fun handleWalletBalances(newWallet: Wallet, oldWallet: Wallet, newAmount: Double, oldAmount: Double, newWalletId: String, type: Int) {
        if (newWalletId == initialWalletID) {
            // Same wallet case
            val difference = newAmount - oldAmount
            if (type == 1 && difference > newWallet.balance.toDoubleOrNull() ?: 0.0) {
                showToast("Giao dịch thất bại: Số dư không đủ.")
                return
            }
            val newBalance = if (type == 1) {
                newWallet.balance.toDoubleOrNull()?.minus(difference)
            } else {
                newWallet.balance.toDoubleOrNull()?.plus(difference)
            }
            newBalance?.let { walletViewModel.updateWalletBalance(newWalletId, it) }
        } else {
            // Different wallet case
            handleDifferentWallets(newWallet, oldWallet, newAmount, oldAmount, type)
        }
    }

    private fun handleDifferentWallets(newWallet: Wallet, oldWallet: Wallet, newAmount: Double, oldAmount: Double, type: Int) {
        val newWalletBalance = newWallet.balance.toDoubleOrNull() ?: 0.0
        val oldWalletBalance = oldWallet.balance.toDoubleOrNull() ?: 0.0
        val isExpense = type == 1
        val newWalletNewBalance = if (isExpense) newWalletBalance - newAmount else newWalletBalance + newAmount
        val oldWalletNewBalance = if (isExpense) oldWalletBalance + oldAmount else oldWalletBalance - oldAmount

        if (newWalletNewBalance < 0) {
            showToast("Giao dịch thất bại: Số dư không đủ trong ví mới.")
            return
        }

        walletViewModel.updateWalletBalance(newWallet.walletID, newWalletNewBalance)
        initialWalletID?.let { walletViewModel.updateWalletBalance(it, oldWalletNewBalance) }
    }

    private fun createTransaction(newWalletId: String, newCategoryId: Int, note: String, newAmount: Double, type: Int): Transaction {
        val sdf = SimpleDateFormat("HH:mm, EEEE, dd MMMM yyyy", Locale("vi", "VN"))
        val dateStr = binding.tvTransactionTime.text.toString()
        val date = sdf.parse(dateStr) ?: calendar.time

        return Transaction(
            transactionID = currentTransaction?.transactionID ?: "",
            walletID = newWalletId,
            categoryID = newCategoryId,
            note = note,
            type = type,
            amount = newAmount,
            date = Timestamp(date),
            hour = SimpleDateFormat("HH:mm", Locale("vi", "VN")).format(date),
            userID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        )
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

    private fun disableUserInteractions() {
        binding.etTransactionAmount.isEnabled = false
        binding.etTransactionAmount.isFocusable = false
        binding.etTransactionAmount.isFocusableInTouchMode = false
        binding.chooseCategory.isEnabled = false
        binding.chooseWallet.isEnabled = false
        binding.tvTransactionTime.isEnabled = false
        binding.tvTranContent.isEnabled = false
    }

    private fun hideTrashButton() {
        binding.btTrash.isVisible = false
        binding.btSaveTransaction.isVisible = false
    }

    private fun setTransactionUI(iconResId: Int, title: String, description: String) {
        binding.ivTransactionIcon.setImageResource(iconResId)
        binding.tvTransactionTitle.text = title
        binding.textView.text = description
    }
}
