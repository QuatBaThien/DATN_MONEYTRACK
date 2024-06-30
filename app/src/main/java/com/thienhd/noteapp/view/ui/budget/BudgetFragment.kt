package com.thienhd.noteapp.view.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.thienhd.noteapp.R
import com.thienhd.noteapp.databinding.FragmentBudgetBinding
import com.thienhd.noteapp.viewmodel.BudgetViewModel
import com.thienhd.noteapp.viewmodel.CategoryViewModel
import com.thienhd.noteapp.viewmodel.TransactionViewModel
import com.thienhd.noteapp.view.ui.budget.adapter.BudgetAdapter

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private val budgetViewModel: BudgetViewModel by activityViewModels()
    private val categoryViewModel: CategoryViewModel by activityViewModels()
    private val transactionViewModel: TransactionViewModel by activityViewModels()
    private lateinit var budgetAdapter: BudgetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        budgetViewModel.setCategoryViewModel(categoryViewModel)
        budgetViewModel.setTransactionViewModel(transactionViewModel)

        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btCreate.setOnClickListener {
            findNavController().navigate(R.id.action_budgetFragment_to_createBudgetFragment)
        }

        binding.btExpenseBudget.setOnClickListener {
            setSelectedButton(binding.btExpenseBudget)
            setUnselectedButton(binding.btIncomeBudget)
            budgetViewModel.filterBudgetsByType(1) // Expense
        }

        binding.btIncomeBudget.setOnClickListener {
            setSelectedButton(binding.btIncomeBudget)
            setUnselectedButton(binding.btExpenseBudget)
            budgetViewModel.filterBudgetsByType(0) // Income
        }

        budgetAdapter = BudgetAdapter(categoryViewModel)
        binding.rvBudgets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBudgets.adapter = budgetAdapter

        budgetViewModel.filteredBudgets.observe(viewLifecycleOwner) { budgets ->
            budgetAdapter.submitList(budgets)
        }

        // Default to showing expense budgets on fragment start
        budgetViewModel.filterBudgetsByType(1)
        setSelectedButton(binding.btExpenseBudget)
        setUnselectedButton(binding.btIncomeBudget)
    }

    private fun setSelectedButton(button: MaterialButton) {
        button.isSelected = true
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    private fun setUnselectedButton(button: MaterialButton) {
        button.isSelected = false
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
