package com.thienhd.noteapp.view.ui.menu.debt

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.thienhd.noteapp.R
import com.thienhd.noteapp.data.entities.Debt
import com.thienhd.noteapp.data.entities.Loan
import com.thienhd.noteapp.databinding.FragmentCreateDebtBinding
import com.thienhd.noteapp.viewmodel.DebtViewModel
import com.google.firebase.Timestamp
import com.thienhd.noteapp.data.entities.Transaction
import com.thienhd.noteapp.viewmodel.ChooseWalletViewModel
import com.thienhd.noteapp.viewmodel.TransactionViewModel
import com.thienhd.noteapp.viewmodel.WalletViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class CreateDebtFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private var _binding: FragmentCreateDebtBinding? = null
    private val binding get() = _binding!!
    private val debtViewModel: DebtViewModel by activityViewModels()
    private val chooseWalletViewModel: ChooseWalletViewModel by activityViewModels()
    private val walletViewModel: WalletViewModel by activityViewModels()
    private val transactionViewModel: TransactionViewModel by activityViewModels()

    private val calendarTransaction: Calendar = Calendar.getInstance()
    private val calendarDeadline: Calendar = Calendar.getInstance()

    private var isDebt = true // Track whether it's a debt or loan
    private var isUndeadline = 0 // Track whether it's an undated debt
    private var isStartDate = true
    private var isWalletSelected = false
    private var isWalletAmount = false
    private var isLender = false
    private var isReset = true
    private var walletID = ""
    private var newBalance = 0.0
    val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateDebtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button click listener
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Radio group listener to switch between debt and loan
        binding.rgCategoryType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_debt -> {
                    isDebt = true
                    binding.tvLabelLender.text = "Người cho vay"
                    binding.tvLender.hint = "Người cho vay"
                }
                R.id.rb_loan -> {
                    isDebt = false
                    binding.tvLabelLender.text = "Người mượn"
                    binding.tvLender.hint = "Người mượn"
                }
            }
        }

        // Checkbox listener to toggle deadline visibility
        binding.cbDeadline.setOnCheckedChangeListener { _, isChecked ->
            isUndeadline = if (isChecked) 1 else 0
            binding.tvDeadlineTime.visibility = if (isChecked) View.GONE else View.VISIBLE
            updateSaveButtonState()
        }

        // Text watcher to enable the create button when input is valid
        binding.etTransactionAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isWalletAmount = !s.isNullOrEmpty()
                updateSaveButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.tvLender.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isLender = !s.isNullOrEmpty()
                updateSaveButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Set default current date and time
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("vi", "VN"))
        binding.tvTransactionTime.text = sdf.format(calendarTransaction.time)
        binding.tvDeadlineTime.text = sdf.format(calendarDeadline.time)

        binding.tvTransactionTime.setOnClickListener {
            isStartDate = true
            showDatePicker()
        }
        binding.tvDeadlineTime.setOnClickListener {
            isStartDate = false
            showDatePicker()
        }

        //Choose wallet
        binding.chooseWallet.setOnClickListener {
            isReset = false
            findNavController().navigate(R.id.action_createDebtFragment_to_chooseWalletFragment)
        }

        chooseWalletViewModel.walletId.observe(viewLifecycleOwner) { walletId ->
            if (walletId != null) {
                updateWalletUI(walletId)
                isWalletSelected = true
                walletID = walletId
            } else {
                resetWalletUI()
                isWalletSelected = false
            }
            updateSaveButtonState()
        }

        // Create transaction button click listener
        binding.btCreateTransaction.setOnClickListener {
            createTransaction()
        }
    }

    private fun updateWalletUI(walletId: String) {
        val wallet = walletViewModel.getWalletByWalletID(walletId)
        wallet?.let {
            binding.tvWalletTitle.text = it.name
            binding.tvWalletBalance.text = numberFormat.format(it.balance) + " VNĐ"
        }
    }

    private fun resetWalletUI() {
        binding.tvWalletTitle.text = getString(R.string.default_wallet_text)
        binding.tvWalletBalance.text = ""
    }

    private fun updateSaveButtonState() {
        val isDateCheck = isUndeadline == 1 || calendarDeadline.time.after(calendarTransaction.time)
        val hasChanges = isWalletSelected && isWalletAmount && isDateCheck && isLender
        binding.btCreateTransaction.isEnabled = hasChanges
        binding.btCreateTransaction.backgroundTintList = if (hasChanges) {
            ContextCompat.getColorStateList(requireContext(), R.color.blue)
        } else {
            ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
        }
    }

    private fun updateDateTimeDisplay() {
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("vi", "VN"))
        if (isStartDate) {
            binding.tvTransactionTime.text = sdf.format(calendarTransaction.time)
        } else {
            binding.tvDeadlineTime.text = sdf.format(calendarDeadline.time)
        }
        updateSaveButtonState()
    }

    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            this,
            if (isStartDate) calendarTransaction.get(Calendar.YEAR) else calendarDeadline.get(Calendar.YEAR),
            if (isStartDate) calendarTransaction.get(Calendar.MONTH) else calendarDeadline.get(Calendar.MONTH),
            if (isStartDate) calendarTransaction.get(Calendar.DAY_OF_MONTH) else calendarDeadline.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        if (isStartDate) {
            calendarTransaction.set(Calendar.YEAR, year)
            calendarTransaction.set(Calendar.MONTH, month)
            calendarTransaction.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        } else {
            calendarDeadline.set(Calendar.YEAR, year)
            calendarDeadline.set(Calendar.MONTH, month)
            calendarDeadline.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        updateDateTimeDisplay()
    }

    private fun createTransaction() {
        val amount = binding.etTransactionAmount.text.toString().toDoubleOrNull() ?: return
        val lender = binding.tvLender.text.toString().trim()
        val dateBorrowed = Timestamp(calendarTransaction.time)
        val dateDue = if (isUndeadline == 1) Timestamp.now() else Timestamp(calendarDeadline.time)
        val wallet = walletViewModel.getWalletByWalletID(walletID) ?: return
        if (isDebt) {
            val debt = Debt(
                userID = debtViewModel.getCurrentUserId(),
                lender = lender,
                amount = amount,
                dateBorrowed = dateBorrowed,
                dateDue = dateDue,
                isUndeadline = isUndeadline
            )
            debtViewModel.addDebt(debt)
            newBalance = wallet.balance.plus(amount)
            walletViewModel.updateWalletBalance(walletID, newBalance)
            transactionViewModel.addTransaction(
                Transaction(
                    walletID = walletID,
                    categoryID = -3,
                    note = "Vay tiền từ $lender",
                    type = 3,
                    amount = amount,
                    date = dateBorrowed,
                    userID = debtViewModel.getCurrentUserId()
                )
            )
        } else {
            newBalance = wallet.balance.minus(amount)
            if (newBalance >= 0) {
                val loan = Loan(
                    userID = debtViewModel.getCurrentUserId(),
                    borrower = lender,
                    amount = amount,
                    dateBorrowed = dateBorrowed,
                    dateDue = dateDue,
                    isUndeadline = isUndeadline
                )
                debtViewModel.addLoan(loan)
                walletViewModel.updateWalletBalance(walletID, newBalance)
                transactionViewModel.addTransaction(
                    Transaction(
                        walletID = walletID,
                        categoryID = -4,
                        note = "Cho vay: $lender vay tiền",
                        type = 4,
                        amount = amount,
                        date = dateBorrowed,
                        userID = debtViewModel.getCurrentUserId()
                    )
                )
            }else
                Toast.makeText(requireContext(), "Tạo thất bại: Số dư không đủ", Toast.LENGTH_SHORT).show()
        }
        val bundle = Bundle().apply {
            putInt("debtType", if (isDebt) 0 else 1)
        }
        findNavController().navigate(R.id.action_createDebtFragment_to_debtFragment, bundle)
    }

    override fun onResume() {
        super.onResume()
        if (isReset) {
            chooseWalletViewModel.resetWallet()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
