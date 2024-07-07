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
import com.thienhd.noteapp.R
import com.thienhd.noteapp.data.FilterCriteria
import com.thienhd.noteapp.databinding.FragmentTransactionBinding
import com.thienhd.noteapp.view.ui.transaction.adapter.RVTransactionAdapter
import com.thienhd.noteapp.viewmodel.CategoryViewModel
import com.thienhd.noteapp.viewmodel.TransactionViewModel

class TransactionFragment : Fragment(), TransactionFilterBottomSheetFragment.OnFilterAppliedListener {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TransactionViewModel
    private val categoryViewModel: CategoryViewModel by activityViewModels()
    private lateinit var adapter: RVTransactionAdapter
    private var currentFilterCriteria: FilterCriteria? = null

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

        viewModel = TransactionViewModel(requireActivity().application)

        adapter = RVTransactionAdapter(emptyList(), categoryViewModel)
        binding.rvDateTransaction.layoutManager = LinearLayoutManager(context)
        binding.rvDateTransaction.adapter = adapter

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            adapter.submitList(transactions)
        }

        binding.evSearch.setOnFocusChangeListener { _, hasFocus ->
            binding.btCancel.visibility = if (hasFocus) View.VISIBLE else View.INVISIBLE
        }
        binding.btCancel.setOnClickListener {
            binding.evSearch.clearFocus()
            it.hideKeyboard()
        }

        binding.transactionFilter.setOnClickListener {
            val filterFragment = TransactionFilterBottomSheetFragment().apply {
                setOnFilterAppliedListener(this@TransactionFragment)
                arguments = Bundle().apply {
                    putParcelable("ARG_CRITERIA", currentFilterCriteria)
                }
            }
            filterFragment.show(childFragmentManager, filterFragment.tag)
        }

        binding.btSearch.setOnClickListener {
            val query = binding.evSearch.text.toString()
            viewModel.searchTransactions(query)
        }

        binding.btCancel.setOnClickListener {
            binding.evSearch.setText("")
            viewModel.resetTransactions()
            it.hideKeyboard()
        }

        binding.evSearch.setOnFocusChangeListener { _, hasFocus ->
            binding.btCancel.visibility = if (hasFocus && binding.evSearch.text?.isNotEmpty() == true) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun onFilterApplied(criteria: FilterCriteria) {
        currentFilterCriteria = criteria
        viewModel.filterTransactions(criteria)
        updateFilterButtonAppearance(criteria)
    }

    private fun updateFilterButtonAppearance(criteria: FilterCriteria) {
        if (criteria != FilterCriteria(null,null,0,0)) {
            binding.transactionFilter.setBackgroundResource(R.drawable.bt_filter_selected)
        } else {
            binding.transactionFilter.setBackgroundResource(R.drawable.button_icon)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentFilterCriteria.observe(viewLifecycleOwner) { criteria ->
            updateFilterButtonAppearance(criteria)
        }
    }
    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}
