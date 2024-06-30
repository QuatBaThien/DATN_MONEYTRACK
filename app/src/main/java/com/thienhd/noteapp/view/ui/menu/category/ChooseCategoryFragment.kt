package com.thienhd.noteapp.view.ui.menu.category

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.thienhd.noteapp.R
import com.thienhd.noteapp.databinding.FragmentChooseCategoryBinding
import com.thienhd.noteapp.view.ui.menu.category.adapter.CategoryAdapter
import com.thienhd.noteapp.viewmodel.CategoryViewModel
import com.thienhd.noteapp.viewmodel.ChooseCategoryViewModel

class ChooseCategoryFragment : Fragment() {
    private val viewModel: CategoryViewModel by activityViewModels()
    private val chooseCategoryViewModel: ChooseCategoryViewModel by activityViewModels()
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var binding: FragmentChooseCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadCategoriesFromFirestore()
        categoryAdapter = CategoryAdapter(emptyList()) { category ->
            chooseCategoryViewModel.setCategoryId(category.categoryID)
            findNavController().navigateUp()
        }

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = categoryAdapter
        }

        viewModel.expenseCategories.observe(viewLifecycleOwner) { categories ->
            Log.d("CategoryFragment", "Expense categories updated: ${categories.size} items")
            if (binding.btExpense.isSelected) {
                categoryAdapter.submitList(categories)
            }
        }

        viewModel.incomeCategories.observe(viewLifecycleOwner) { categories ->
            Log.d("CategoryFragment", "Income categories updated: ${categories.size} items")
            if (binding.btIncome.isSelected) {
                categoryAdapter.submitList(categories)
            }
        }

        binding.btExpense.setOnClickListener {
            setSelectedButton(binding.btExpense)
            setUnselectedButton(binding.btIncome)
            viewModel.expenseCategories.value?.let {
                Log.d("CategoryFragment", "Displaying ${it.size} expense categories")
                categoryAdapter.submitList(it)
            }
        }

        binding.btIncome.setOnClickListener {
            setSelectedButton(binding.btIncome)
            setUnselectedButton(binding.btExpense)
            viewModel.incomeCategories.value?.let {
                Log.d("CategoryFragment", "Displaying ${it.size} income categories")
                categoryAdapter.submitList(it)
            }
        }

        binding.btExpense.isSelected = true // Default selection
        setSelectedButton(binding.btExpense) // Set initial selected button
    }

    private fun setSelectedButton(button: MaterialButton) {
        button.isSelected = true
        button.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.blue)
    }

    private fun setUnselectedButton(button: MaterialButton) {
        button.isSelected = false
        button.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.stroke_color)
    }
}
