package com.thienhd.noteapp.view.ui.transaction

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.thienhd.noteapp.databinding.FragmentTransactionBinding
import com.thienhd.noteapp.view.ui.transaction.adapter.RVTransactionAdapter
import com.thienhd.noteapp.viewmodel.CategoryViewModel
import com.thienhd.noteapp.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

class TransactionFragment : Fragment() {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionViewModel by activityViewModels()
    private val categoryViewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RVTransactionAdapter(emptyList(), categoryViewModel)
        binding.rvDateTransaction.layoutManager = LinearLayoutManager(context)
        binding.rvDateTransaction.adapter = adapter

        viewModel.transactions.observe(viewLifecycleOwner, { transactions ->
            adapter.submitList(transactions)
        })

        binding.fromDate.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Từ ngày")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setPositiveButtonText("Chọn")
                    .setNegativeButtonText("Huỷ")
                    .build()
            datePicker.addOnPositiveButtonClickListener {
                binding.fromDate.text = SimpleDateFormat("dd-MM-yyyy", Locale("vi", "VN")).format(Date(it))
                // Optionally, call the filter function here
            }
            datePicker.show(childFragmentManager, "picker")
        }

        binding.toDate.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Đến ngày")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setPositiveButtonText("Chọn")
                    .setNegativeButtonText("Huỷ")
                    .build()
            datePicker.addOnPositiveButtonClickListener {
                binding.toDate.text = SimpleDateFormat("dd-MM-yyyy", Locale("vi", "VN")).format(Date(it))
                // Optionally, call the filter function here
            }
            datePicker.show(childFragmentManager, "picker")
        }

        binding.evSearch.setOnFocusChangeListener { _, hasFocus ->
            binding.btCancel.visibility = if (hasFocus) View.VISIBLE else View.INVISIBLE
        }
        binding.btCancel.setOnClickListener {
            binding.evSearch.clearFocus()
            it.hideKeyboard()
        }

        binding.transactionFilter.setOnClickListener {
            val filterFragment = TransactionFilterBottomSheetFragment()
            filterFragment.show(childFragmentManager, filterFragment.tag)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}
