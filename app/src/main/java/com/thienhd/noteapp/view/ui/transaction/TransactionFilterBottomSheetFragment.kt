package com.thienhd.noteapp.view.ui.transaction

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.thienhd.noteapp.R
import com.thienhd.noteapp.data.FilterCriteria
import com.thienhd.noteapp.databinding.FragmentTransactionFilterBottomSheetBinding
import java.text.SimpleDateFormat
import java.util.*

class TransactionFilterBottomSheetFragment : BottomSheetDialogFragment(), DatePickerDialog.OnDateSetListener {

    private var _binding: FragmentTransactionFilterBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var isStartDate = true
    private var fromDate: Date? = null
    private var toDate: Date? = null
    private var type: Int = 0 // Default to all
    private var order: Int = 0 // Default to newest
    private val calendar = Calendar.getInstance()

    interface OnFilterAppliedListener {
        fun onFilterApplied(criteria: FilterCriteria)
    }

    private var listener: OnFilterAppliedListener? = null

    fun setOnFilterAppliedListener(listener: OnFilterAppliedListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val criteria = arguments?.getParcelable<FilterCriteria>("ARG_CRITERIA")
        criteria?.let { setInitialCriteria(it) }

        binding.fromDate.setOnClickListener {
            isStartDate = true
            showDatePicker()
        }

        binding.toDate.setOnClickListener {
            isStartDate = false
            showDatePicker()
        }

        binding.btIncome.setOnClickListener {
            type = 1
            setSelectedButton(binding.btIncome)
            setUnselectedButton(binding.btExpenses, binding.btAll)
        }

        binding.btExpenses.setOnClickListener {
            type = 2
            setSelectedButton(binding.btExpenses)
            setUnselectedButton(binding.btIncome, binding.btAll)
        }

        binding.btAll.setOnClickListener {
            type = 0
            setSelectedButton(binding.btAll)
            setUnselectedButton(binding.btIncome, binding.btExpenses)
        }

        binding.btNew.setOnClickListener {
            order = 0
            setSelectedButton(binding.btNew)
            setUnselectedButton(binding.btOld)
        }

        binding.btOld.setOnClickListener {
            order = 1
            setSelectedButton(binding.btOld)
            setUnselectedButton(binding.btNew)
        }

        binding.btnApply.setOnClickListener {
            val criteria = FilterCriteria(fromDate, toDate, type, order)
            listener?.onFilterApplied(criteria)
            dismiss()
        }

        binding.btReset.setOnClickListener {
            resetFilters()
        }
    }

    private fun showDatePicker() {
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
        updateDateDisplay()
    }

    private fun updateDateDisplay() {
        val sdf = SimpleDateFormat("dd-MM-yy", Locale("vi", "VN"))
        if (isStartDate) {
            fromDate = calendar.time
            binding.fromDate.text = sdf.format(calendar.time)
        } else {
            toDate = calendar.time
            binding.toDate.text = sdf.format(calendar.time)
        }
    }

    private fun setInitialCriteria(criteria: FilterCriteria) {
        val sdf = SimpleDateFormat("dd-MM-yy", Locale("vi", "VN"))
        fromDate = criteria.startDate
        toDate = criteria.endDate
        type = criteria.type ?: 0
        order = criteria.order ?: 0

        fromDate?.let {
            binding.fromDate.text = sdf.format(it)
        }
        toDate?.let {
            binding.toDate.text = sdf.format(it)
        }

        when (type) {
            1 -> setSelectedButton(binding.btIncome)
            2 -> setSelectedButton(binding.btExpenses)
            0 -> setSelectedButton(binding.btAll)
        }


        when (order) {
            1 -> setSelectedButton(binding.btOld)
           0 -> setSelectedButton(binding.btNew)
        }
    }

    private fun setSelectedButton(vararg buttons: MaterialButton) {
        buttons.forEach {button ->
            button.isSelected = true
            button.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.blue)
        }
    }

    private fun setUnselectedButton(vararg buttons: MaterialButton) {
        buttons.forEach { button ->
            button.isSelected = false
            button.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.stroke_color)
        }
    }

    private fun resetFilters() {
        fromDate = null
        toDate = null
        type = 0
        order = 0
        binding.fromDate.text = getString(R.string.from_date)
        binding.toDate.text = getString(R.string.to_date)
        setUnselectedButton(binding.btIncome, binding.btExpenses, binding.btOld)
        setSelectedButton(binding.btAll, binding.btNew)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
