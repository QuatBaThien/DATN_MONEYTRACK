package com.thienhd.noteapp.view.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.thienhd.noteapp.R
import com.thienhd.noteapp.databinding.FragmentCategorySelectionBottomSheetBinding
import com.thienhd.noteapp.view.ui.transaction.adapter.ListCategoryAdapter
import com.thienhd.noteapp.viewmodel.CategoryViewModel

class CategorySelectionBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCategorySelectionBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by activityViewModels()

    private lateinit var listCategoryAdapter: ListCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategorySelectionBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listCategoryAdapter = ListCategoryAdapter(emptyList()) { category ->
            parentFragmentManager.setFragmentResult("requestKey", Bundle().apply {
                putInt("selectedCategoryId", category.categoryID)
            })
            dismiss()
        }

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listCategoryAdapter
        }
        binding.btExpense.setOnClickListener {
            viewModel.expenseCategories.value?.let { categories ->
                listCategoryAdapter.submitList(categories)
            }
        }

        binding.btIncome.setOnClickListener {
            viewModel.incomeCategories.value?.let { categories ->
                listCategoryAdapter.submitList(categories)
            }
        }

        viewModel.expenseCategories.observe(viewLifecycleOwner) { categories ->
            if (binding.btExpense.isSelected) {
                listCategoryAdapter.submitList(categories)
            }
        }

        viewModel.incomeCategories.observe(viewLifecycleOwner) { categories ->
            if (binding.btIncome.isSelected) {
                listCategoryAdapter.submitList(categories)
            }
        }
        binding.btExpense.setOnClickListener {
            setSelectedButton(binding.btExpense)
            setUnselectedButton(binding.btIncome)
            viewModel.expenseCategories.value?.let { it1 -> listCategoryAdapter.submitList(it1) }
        }

        binding.btIncome.setOnClickListener {
            setSelectedButton(binding.btIncome)
            setUnselectedButton(binding.btExpense)
            viewModel.incomeCategories.value?.let { it1 -> listCategoryAdapter.submitList(it1) }
        }
        // Default selection
        binding.btExpense.isSelected = true
        viewModel.expenseCategories.value?.let { listCategoryAdapter.submitList(it) }
    }
    private fun setSelectedButton(button: MaterialButton) {
        button.isSelected = true
        button.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.blue)
    }

    private fun setUnselectedButton(button: MaterialButton) {
        button.isSelected = false
        button.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.stroke_color)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
