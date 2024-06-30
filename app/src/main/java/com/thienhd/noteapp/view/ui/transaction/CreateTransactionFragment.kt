package com.thienhd.noteapp.view.ui.transaction

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.thienhd.noteapp.R
import com.thienhd.noteapp.data.entities.Transaction
import com.thienhd.noteapp.databinding.FragmentCreateTransactionBinding
import com.thienhd.noteapp.viewmodel.CategoryViewModel
import com.thienhd.noteapp.viewmodel.ChooseCategoryViewModel
import com.thienhd.noteapp.viewmodel.ChooseWalletViewModel
import com.thienhd.noteapp.viewmodel.CreateTransactionViewModel
import com.thienhd.noteapp.viewmodel.TransactionViewModel
import com.thienhd.noteapp.viewmodel.WalletViewModel
import java.text.SimpleDateFormat
import java.util.*

class CreateTransactionFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private var _binding: FragmentCreateTransactionBinding? = null
    private val binding get() = _binding!!
    private var isCategorySelected = false
    private var isWalletSelected = false
    private var isWalletAmount = false

    private val categoryViewModel: CategoryViewModel by activityViewModels()
    private val walletViewModel: WalletViewModel by activityViewModels()
    private val transactionViewModel: TransactionViewModel by activityViewModels()
    private val chooseCategoryViewModel: ChooseCategoryViewModel by activityViewModels()
    private val chooseWalletViewModel: ChooseWalletViewModel by activityViewModels()
    private val calendar: Calendar = Calendar.getInstance()
    private var isReset = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chooseCategoryViewModel.categoryId.observe(viewLifecycleOwner) { categoryId ->
            if (categoryId != null) {
                updateCategoryUI(categoryId)
                isCategorySelected = true
            } else {
                resetCategoryUI()
                isCategorySelected = false
            }
            updateSaveButtonState()
        }

        chooseWalletViewModel.walletId.observe(viewLifecycleOwner) { walletId ->
            if (walletId != null) {
                updateWalletUI(walletId)
                isWalletSelected = true
            } else {
                resetWalletUI()
                isWalletSelected = false
            }
            updateSaveButtonState()
        }

        binding.chooseCategory.setOnClickListener {
            findNavController().navigate(R.id.action_createTransactionFragment_to_chooseCategoryFragment)
            isReset = false
        }

        binding.chooseWallet.setOnClickListener {
            findNavController().navigate(R.id.action_createTransactionFragment_to_chooseWalletFragment)
            isReset = false
        }

        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvTransactionTime.setOnClickListener {
            showDateTimePicker()
        }

        // Set default current date and time
        updateDateTimeDisplay(calendar)

        // Add text watcher to update save button state when amount is changed
        binding.etTransactionAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isWalletAmount = !s.isNullOrEmpty()
                updateSaveButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btCreateTransaction.setOnClickListener {
            createTransaction()
        }

        updateSaveButtonState()
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
        val hasChanges = isWalletSelected && isCategorySelected && isWalletAmount
        binding.btCreateTransaction.isEnabled = hasChanges
        binding.btCreateTransaction.backgroundTintList = if (hasChanges) {
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
    }

    private fun createTransaction() {
        val walletId = chooseWalletViewModel.walletId.value ?: return
        val categoryId = chooseCategoryViewModel.categoryId.value ?: return
        val amountStr = binding.etTransactionAmount.text.toString()
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        val note = binding.tvTranContent.text.toString()
        val date = Timestamp(calendar.time) // This is the timestamp we need
        val time = binding.tvTransactionTime.text.toString().split(",")[0]

        val wallet = walletViewModel.getWalletByWalletID(walletId) ?: return
        val category = categoryViewModel.getCategoryById(categoryId) ?: return

        val newBalance = if (category.type == 1) {
            wallet.balance.toDoubleOrNull()?.minus(amount)
        } else {
            wallet.balance.toDoubleOrNull()?.plus(amount)
        }

        if (newBalance != null && newBalance >= 0) {
            val userId = wallet.userID
            val transaction = Transaction(
                walletID = walletId,
                categoryID = categoryId,
                note = note,
                type = category.type,
                amount = amountStr,
                date = date,
                hour = time,
                userID = userId
            )

            transactionViewModel.addTransaction(transaction)
            walletViewModel.updateWalletBalance(walletId, newBalance)
            findNavController().navigateUp()
        } else {
            Toast.makeText(requireContext(), "Giao dịch thất bại: Số dư không đủ", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        isReset = true
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
