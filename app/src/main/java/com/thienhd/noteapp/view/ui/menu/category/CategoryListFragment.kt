package com.thienhd.noteapp.view.ui.menu.category

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
import com.thienhd.noteapp.databinding.FragmentCategoryListBinding
import com.thienhd.noteapp.view.ui.transaction.adapter.ListCategoryAdapter
import com.thienhd.noteapp.viewmodel.CategoryViewModel

class CategoryListFragment : Fragment() {

    private val viewModel: CategoryViewModel by activityViewModels()
    private lateinit var listCategoryAdapter: ListCategoryAdapter
    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listCategoryAdapter = ListCategoryAdapter(emptyList()) { category ->
            val bundle = Bundle().apply {
                putInt("selectedCategoryId", category.categoryID)
            }
             findNavController().navigate(R.id.action_categoryListFragment_to_editTransactionFragment)
        }

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listCategoryAdapter
        }

        binding.btExpense.isSelected = true // Default selection
        setSelectedButton(binding.btExpense) // Set initial selected button
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
            viewModel.expenseCategories.value?.let { categories -> listCategoryAdapter.submitList(categories) }
        }

        binding.btIncome.setOnClickListener {
            setSelectedButton(binding.btIncome)
            setUnselectedButton(binding.btExpense)
            viewModel.incomeCategories.value?.let { categories -> listCategoryAdapter.submitList(categories) }
        }

        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }
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