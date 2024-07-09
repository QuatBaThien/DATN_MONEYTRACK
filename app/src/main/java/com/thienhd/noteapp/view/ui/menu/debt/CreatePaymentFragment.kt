package com.thienhd.noteapp.view.ui.menu.debt

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.Timestamp
import com.thienhd.noteapp.R
import com.thienhd.noteapp.data.entities.Debt
import com.thienhd.noteapp.data.entities.Loan
import com.thienhd.noteapp.data.entities.Transaction
import com.thienhd.noteapp.databinding.FragmentCreatePaymentBinding
import com.thienhd.noteapp.viewmodel.ChooseWalletViewModel
import com.thienhd.noteapp.viewmodel.DebtViewModel
import com.thienhd.noteapp.viewmodel.TransactionViewModel
import com.thienhd.noteapp.viewmodel.WalletViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class CreatePaymentFragment : Fragment() {

    private var _binding: FragmentCreatePaymentBinding? = null
    private val binding get() = _binding!!
    private val debtViewModel: DebtViewModel by activityViewModels()
    private val chooseWalletViewModel: ChooseWalletViewModel by activityViewModels()
    private val walletViewModel: WalletViewModel by activityViewModels()
    private val transactionViewModel: TransactionViewModel by activityViewModels()
    private val args: CreatePaymentFragmentArgs by navArgs()
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("vi", "VN"))
    val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))

    private var isWalletSelected = false
    private var isWalletAmount = false
    private var isLender = false
    private var isReset = true
    private var debtId = ""
    private var debtType = ""
    private lateinit var debtInfo: Any
    private var debtAmount = 0.0
    private var debtPaidAmount = 0.0
    private var debtPeople = ""
    private var debtBorrowed: Timestamp = Timestamp.now()
    private var debtDue: Timestamp = Timestamp.now()
    private var isUndeadline = -1

    private var walletID = ""
    private var newBalance = 0.0
    private var payAmount = 0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        debtId = args.debtId
        debtType = args.debtType

        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Choose wallet
        binding.chooseWallet.setOnClickListener {
            isReset = false
            findNavController().navigate(R.id.action_createPaymentFragment_to_chooseWalletFragment)
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

        // Text watcher to format the amount and validate input
        binding.etPayAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isWalletAmount = !s.isNullOrEmpty()
                updateSaveButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Hide keyboard and format amount when EditText loses focus
        binding.etPayAmount.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
                formatAmount()
            }
        }

        binding.evLender.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isLender = !s.isNullOrEmpty()
                updateSaveButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btCreateTransaction.setOnClickListener {
            createTransaction()
        }

        // Populate UI with data passed from DebtFragment
        debtInfo = debtViewModel.getDebtById(debtId, debtType)!!
        debtInfo.let {
            when (it) {
                is Debt -> {
                    debtAmount = it.amount
                    debtPaidAmount = it.paidAmount
                    debtDue = it.dateDue
                    debtBorrowed = it.dateBorrowed
                    debtPeople = it.lender
                    isUndeadline = it.isUndeadline

                    binding.apply {
                        tvMaxPay.text = "/" + numberFormat.format(debtAmount - debtPaidAmount) + " VNĐ"
                        tvCategoryTitle.text = "Trả nợ"
                        val iconResId = root.context.resources.getIdentifier("ic_item_paid_debt", "drawable", root.context.packageName)
                        ivCategoryIcon.setImageResource(iconResId)
                        tvTransactionTime.text = dateFormat.format(it.dateBorrowed.toDate())
                        evLender.setText("Trả nợ cho ${it.lender}")
                    }
                }

                is Loan -> {
                    debtAmount = it.amount
                    debtPaidAmount = it.paidAmount
                    debtDue = it.dateDue
                    debtBorrowed = it.dateBorrowed
                    debtPeople = it.borrower
                    isUndeadline = it.isUndeadline

                    binding.apply {
                        tvMaxPay.text = "/" + numberFormat.format(debtAmount - debtPaidAmount) + " VNĐ"
                        tvCategoryTitle.text = "Thu nợ"
                        val iconResId = root.context.resources.getIdentifier("ic_item_get_paid", "drawable", root.context.packageName)
                        ivCategoryIcon.setImageResource(iconResId)
                        tvTransactionTime.text = dateFormat.format(it.dateBorrowed.toDate())
                        evLender.setText("Trả nợ cho ${it.borrower}")
                    }
                }

                else -> {}
            }
        }

        // Set touch listener on root view to hide keyboard when clicking outside EditText
        binding.root.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard(v)
                binding.root.clearFocus()
            }
            false
        }
    }

    private fun createTransaction() {
        val wallet = walletViewModel.getWalletByWalletID(walletID) ?: return
        newBalance = wallet.balance.toDoubleOrNull()?.minus(payAmount)!!
        if (newBalance < 0) {
            Toast.makeText(requireContext(), "Tạo thất bại: Số dư không đủ", Toast.LENGTH_SHORT).show()
            return
        }

        if (debtType == "debt") {
            val debt = Debt(
                debtID = debtId,
                userID = debtViewModel.getCurrentUserId(),
                lender = debtPeople,
                amount = debtAmount,
                paidAmount = debtPaidAmount + payAmount,
                dateBorrowed = debtBorrowed,
                dateDue = debtDue,
                isUndeadline = isUndeadline
            )
            debtViewModel.updateDebt(debt)
        } else {
            val loan = Loan(
                loanID = debtId,
                userID = debtViewModel.getCurrentUserId(),
                borrower = debtPeople,
                amount = debtAmount,
                paidAmount = debtPaidAmount + payAmount,
                dateBorrowed = debtBorrowed,
                dateDue = debtDue,
                isUndeadline = isUndeadline
            )
            debtViewModel.updateLoan(loan)
        }

        walletViewModel.updateWalletBalance(walletID, newBalance)
        transactionViewModel.addTransaction(
            Transaction(
                walletID = walletID,
                categoryID = 0,
                note = if (debtType == "debt") "Trả nợ cho $debtPeople" else "Thu nợ của $debtPeople",
                type = if (debtType == "debt") 5 else 6,
                amount = payAmount,
                date = Timestamp.now(),
                userID = debtViewModel.getCurrentUserId()
            )
        )

        val bundle = Bundle().apply {
            putInt("debtType", if (debtType == "debt") 0 else 1)
        }
        findNavController().navigate(R.id.action_createPaymentFragment_to_debtFragment, bundle)
    }

    private fun formatAmount() {
        val enteredAmount = binding.etPayAmount.text.toString().toDoubleOrNull() ?: 0.0
        val maxAmount = debtAmount - debtPaidAmount
        val finalAmount = if (enteredAmount > maxAmount) maxAmount else enteredAmount

        val formattedAmount = numberFormat.format(finalAmount)
        binding.etPayAmount.setText(formattedAmount)
        binding.etPayAmount.setSelection(formattedAmount.length)
        payAmount = finalAmount
        isWalletAmount = finalAmount > 0
        updateSaveButtonState()
    }

    private fun updateWalletUI(walletId: String) {
        val wallet = walletViewModel.getWalletByWalletID(walletId)
        wallet?.let {
            binding.tvWalletTitle.text = it.name
            binding.tvWalletBalance.text = it.balance
        }
    }

    private fun resetWalletUI() {
        binding.tvWalletTitle.text = getString(R.string.default_wallet_text)
        binding.tvWalletBalance.text = ""
    }

    private fun updateSaveButtonState() {
        val hasChanges = isWalletSelected && isWalletAmount && isLender
        binding.btCreateTransaction.isEnabled = hasChanges
        binding.btCreateTransaction.backgroundTintList = if (hasChanges) {
            ContextCompat.getColorStateList(requireContext(), R.color.blue)
        } else {
            ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
        }
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

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
