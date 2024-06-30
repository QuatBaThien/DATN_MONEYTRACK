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
import com.thienhd.noteapp.R
import com.thienhd.noteapp.databinding.FragmentCreateCategoryBinding
import com.thienhd.noteapp.data.entities.Category
import com.thienhd.noteapp.view.ui.menu.category.adapter.IconAdapter
import com.thienhd.noteapp.viewmodel.CategoryViewModel

class CreateCategoryFragment : Fragment() {

    private var _binding: FragmentCreateCategoryBinding? = null
    private val binding get() = _binding!!
    private val iconList = (1..20).map { "ic_item_$it" }
    private var cateIcon: Int? = null
    private val viewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateCategoryBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val iconAdapter = IconAdapter(iconList, null) { iconName, iconIndex ->
            cateIcon = iconIndex
            viewModel.selectIcon(iconIndex)
            updateCreateButtonState()
        }

        binding.recyclerViewIcons.apply {
            layoutManager = GridLayoutManager(context, 4)
            adapter = iconAdapter
        }

        binding.btCreateCategory.setOnClickListener {
            val categoryName = binding.etCategoryName.text.toString()
            val categoryType = if (binding.rbIncome.isChecked) 0 else 1
            val selectedIcon = cateIcon ?: return@setOnClickListener

            val newCategory = Category(
                categoryID = (viewModel.incomeCategories.value?.size ?: 0) + (viewModel.expenseCategories.value?.size ?: 0) + 1,
                name = categoryName,
                type = categoryType,
                iconId = selectedIcon+1,
            )
            viewModel.addCategory(newCategory)
            val bundle = Bundle().apply {
                putInt("categoryType", categoryType)
            }
            findNavController().navigate(R.id.action_createCategoryFragment_to_categoryFragment, bundle)
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

        binding.etCategoryName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateCreateButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.rbIncome.setOnCheckedChangeListener { _, _ -> updateCreateButtonState() }
        binding.rbExpense.setOnCheckedChangeListener { _, _ -> updateCreateButtonState() }

        // Observe the selected icon to update the button state
        viewModel.selectedIcon.observe(viewLifecycleOwner, Observer {
            cateIcon = it
            updateCreateButtonState()
        })
    }

    private fun updateCreateButtonState() {
        val isNameNotEmpty = binding.etCategoryName.text?.isNotEmpty() == true
        val isIconSelected = cateIcon != null
        val isTypeChecked = binding.rbIncome.isChecked || binding.rbExpense.isChecked
        if (isNameNotEmpty && isIconSelected && isTypeChecked) {
            binding.btCreateCategory.isEnabled = true
            binding.btCreateCategory.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
        } else {
            binding.btCreateCategory.isEnabled = false
            binding.btCreateCategory.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.disable_color)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
