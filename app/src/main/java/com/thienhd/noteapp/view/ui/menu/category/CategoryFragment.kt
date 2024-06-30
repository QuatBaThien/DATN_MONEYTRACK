package com.thienhd.noteapp.view.ui.menu.category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.thienhd.noteapp.R
import com.thienhd.noteapp.databinding.FragmentCategoryBinding
import com.thienhd.noteapp.view.ui.menu.category.adapter.CategoryAdapter
import com.thienhd.noteapp.viewmodel.CategoryViewModel

class CategoryFragment : Fragment() {

    private val viewModel: CategoryViewModel by activityViewModels()
    private lateinit var categoryAdapter: CategoryAdapter
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadCategoriesFromFirestore()
        categoryAdapter = CategoryAdapter(emptyList()) { category ->
            val bundle = Bundle().apply {
                 putInt("categoryId", category.categoryID)
            }
            findNavController().navigate(R.id.action_categoryFragment_to_editCategoryFragment, bundle)
        }

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = categoryAdapter
        }

        viewModel.expenseCategories.observe(viewLifecycleOwner, Observer { categories ->
            Log.d("CategoryFragment", "Expense categories updated: ${categories.size} items")
            if (binding.btExpense.isSelected) {
                categoryAdapter.submitList(categories)
            }
        })

        viewModel.incomeCategories.observe(viewLifecycleOwner, Observer { categories ->
            Log.d("CategoryFragment", "Income categories updated: ${categories.size} items")
            if (binding.btIncome.isSelected) {
                categoryAdapter.submitList(categories)
            }
        })

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

        binding.btAddCategory.setOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_createCategoryFragment)
        }

        binding.btBack.setOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_menuFragment)
        }

        binding.btExpense.isSelected = true // Default selection
        setSelectedButton(binding.btExpense) // Set initial selected button

        // Kiểm tra xem có dữ liệu được truyền từ CreateCategoryFragment không
        val categoryType = arguments?.getInt("categoryType")
        if (categoryType != null) {
            if (categoryType == 0) {
                setSelectedButton(binding.btIncome)
                setUnselectedButton(binding.btExpense)
                viewModel.incomeCategories.value?.let { categoryAdapter.submitList(it) }
            } else {
                setSelectedButton(binding.btExpense)
                setUnselectedButton(binding.btIncome)
                viewModel.expenseCategories.value?.let { categoryAdapter.submitList(it) }
            }
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
