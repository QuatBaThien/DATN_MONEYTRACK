package com.thienhd.noteapp.view.ui.budget

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.thienhd.noteapp.R
import com.thienhd.noteapp.data.entities.Budget
import com.thienhd.noteapp.databinding.FragmentCreateBudgetBinding
import com.thienhd.noteapp.viewmodel.BudgetViewModel
import com.thienhd.noteapp.viewmodel.CategoryViewModel
import com.thienhd.noteapp.viewmodel.ChooseCategoryViewModel
import java.text.SimpleDateFormat
import java.util.*

class CreateBudgetFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private var _binding: FragmentCreateBudgetBinding? = null
    private val binding get() = _binding!!
    private val budgetViewModel: BudgetViewModel by activityViewModels()
    private val categoryViewModel: CategoryViewModel by activityViewModels()
    private val chooseCategoryViewModel: ChooseCategoryViewModel by activityViewModels()
    private val calendar: Calendar = Calendar.getInstance()
    private var isStartDate = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chooseCategoryViewModel.categoryId.observe(viewLifecycleOwner) { categoryId ->
            if (categoryId != null) {
                updateCategoryUI(categoryId)
            } else {
                resetCategoryUI()
            }
        }

        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.chooseCategory.setOnClickListener {
            findNavController().navigate(R.id.action_createBudgetFragment_to_chooseCategoryFragment)
        }

        binding.tvDateStartValue.setOnClickListener {
            isStartDate = true
            showDatePicker()
        }

        binding.tvDateEndValue.setOnClickListener {
            isStartDate = false
            showDatePicker()
        }

        binding.etTargetAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateSaveButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btCreateBudget.setOnClickListener {
            createBudget()
        }

        updateSaveButtonState()
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
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("vi", "VN"))
        if (isStartDate) {
            binding.tvDateStartValue.text = sdf.format(calendar.time)
        } else {
            binding.tvDateEndValue.text = sdf.format(calendar.time)
        }
        updateSaveButtonState()
    }

    private fun updateSaveButtonState() {
        val hasChanges = binding.etTargetAmount.text.isNotEmpty() &&
                binding.tvDateStartValue.text != "Chọn ngày bắt đầu" &&
                binding.tvDateEndValue.text != "Chọn ngày kết thúc" &&
                chooseCategoryViewModel.categoryId.value != null
        binding.btCreateBudget.isEnabled = hasChanges
        binding.btCreateBudget.backgroundTintList = if (hasChanges) {
            ContextCompat.getColorStateList(requireContext(), R.color.blue)
        } else {
            ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
        }
    }

    private fun updateCategoryUI(categoryId: Int) {
        val category = categoryViewModel.getCategoryById(categoryId)
        category?.let {
            binding.ivCategoryIcon.setImageResource(getIconResource(it.iconId))
            binding.tvCategoryTitle.text = it.name
        }
    }

    private fun resetCategoryUI() {
        binding.ivCategoryIcon.setImageResource(R.drawable.ic_category)
        binding.tvCategoryTitle.text = getString(R.string.default_category_text)
    }

    private fun getIconResource(iconId: Int): Int {
        val iconName = "ic_item_$iconId"
        return resources.getIdentifier(iconName, "drawable", requireContext().packageName)
    }

    private fun createBudget() {
        val categoryId = chooseCategoryViewModel.categoryId.value ?: return
        val targetAmount = binding.etTargetAmount.text.toString().toDoubleOrNull() ?: return
        val dateStart = SimpleDateFormat("dd MMMM yyyy", Locale("vi", "VN")).parse(binding.tvDateStartValue.text.toString())
        val dateEnd = SimpleDateFormat("dd MMMM yyyy", Locale("vi", "VN")).parse(binding.tvDateEndValue.text.toString())

        val budget = Budget(
            userID = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            categoryID = categoryId,
            targetAmount = targetAmount,
            currentAmount = 0.0,
            dateStart = dateStart,
            dateEnd = dateEnd
        )

        budgetViewModel.addBudget(budget)
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
