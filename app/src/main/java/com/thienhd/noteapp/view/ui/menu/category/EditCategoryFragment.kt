package com.thienhd.noteapp.view.ui.menu.category

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.thienhd.noteapp.R

import com.thienhd.noteapp.data.entities.Category
import com.thienhd.noteapp.databinding.FragmentEditCategoryBinding
import com.thienhd.noteapp.view.ui.menu.category.adapter.IconAdapter
import com.thienhd.noteapp.viewmodel.CategoryViewModel
import com.thienhd.noteapp.viewmodel.TransactionViewModel

class EditCategoryFragment : Fragment() {

    private var _binding: FragmentEditCategoryBinding? = null
    private val binding get() = _binding!!
    private val iconList = (1..20).map { "ic_item_$it" }
    private var cateIcon: Int? = null
    private var cateType: Int? = null
    private var initialCategoryName: String? = null
    private var initialCategoryType: Int? = null
    private var initialCategoryIcon: Int? = null
    private val viewModel: CategoryViewModel by activityViewModels()
    private val transactionViewModel: TransactionViewModel by activityViewModels()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var categoryId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditCategoryBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryId = arguments?.getInt("categoryId") ?: -1
        if (categoryId == -1) {
            findNavController().navigateUp()
            return
        }

        val category = viewModel.getCategoryById(categoryId)

        initialCategoryIcon = category?.iconId?.minus(1)
        initialCategoryType = category?.type
        initialCategoryName = category?.name

        cateIcon = initialCategoryIcon

        val iconAdapter = IconAdapter(iconList, initialCategoryIcon) { iconName, iconIndex ->
            cateIcon = iconIndex
            updateSaveButtonState()
        }

        binding.recyclerViewIcons.apply {
            layoutManager = GridLayoutManager(context, 4)
            adapter = iconAdapter
        }

        binding.etCategoryName.setText(category?.name)
        if (category?.type == 0) {
            binding.rbIncome.isChecked = true
            cateType = 0
        } else {
            binding.rbExpense.isChecked = true
        }

        binding.etCategoryName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateSaveButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.rbIncome.setOnCheckedChangeListener { _, _ -> updateSaveButtonState() }
        binding.rbExpense.setOnCheckedChangeListener { _, _ -> updateSaveButtonState() }

        binding.btSaveCategory.setOnClickListener {
            val categoryName = binding.etCategoryName.text.toString()
            val categoryType = if (binding.rbIncome.isChecked) 0 else 1
            val selectedIcon = cateIcon ?: return@setOnClickListener
            val userId = auth.currentUser!!.uid

            val updatedCategory = Category(
                categoryID = categoryId,
                name = categoryName,
                type = categoryType,
                iconId = selectedIcon+1,
                userID = userId
            )
            viewModel.updateCategory(updatedCategory)
            findNavController().navigateUp()
        }

        binding.btDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.root.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                binding.etCategoryName.clearFocus()
            }
            false
        }

        // Observe selected icon
        viewModel.selectedIcon.observe(viewLifecycleOwner, Observer {
            updateSaveButtonState()
        })

        // Initially disable the save button
        updateSaveButtonState()
    }

    private fun updateSaveButtonState() {
        val isNameChanged = binding.etCategoryName.text.toString() != initialCategoryName
        val isTypeChanged = (binding.rbIncome.isChecked && initialCategoryType != 0) || (binding.rbExpense.isChecked && initialCategoryType != 1)
        val isIconChanged = cateIcon != initialCategoryIcon

        val hasChanges = isNameChanged || isTypeChanged || isIconChanged

        binding.btSaveCategory.isEnabled = hasChanges
        binding.btSaveCategory.backgroundTintList = if (hasChanges) {
            ContextCompat.getColorStateList(requireContext(), R.color.blue)
        } else {
            ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
        }
    }


    private fun showDeleteConfirmationDialog() {
        val dialog = DeleteConfirmationDialogFragment("") {
            val category = viewModel.getCategoryById(categoryId)
            val isIncome = category?.type == 0
            viewModel.deleteCategory(categoryId)
            transactionViewModel.updateTransactionsForDeletedCategory(categoryId, isIncome)
        }
        dialog.show(childFragmentManager, "DeleteConfirmationDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
