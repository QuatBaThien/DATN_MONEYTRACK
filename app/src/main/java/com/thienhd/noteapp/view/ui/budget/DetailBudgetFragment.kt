package com.thienhd.noteapp.view.ui.budget

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.thienhd.noteapp.databinding.FragmentDetailBudgetBinding
import com.thienhd.noteapp.view.ui.budget.adapter.BudgetTransactionAdapter
import com.thienhd.noteapp.viewmodel.BudgetViewModel
import com.thienhd.noteapp.viewmodel.CategoryViewModel
import com.thienhd.noteapp.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class DetailBudgetFragment : Fragment() {

    private var _binding: FragmentDetailBudgetBinding? = null
    private val binding get() = _binding!!
    private val budgetViewModel: BudgetViewModel by activityViewModels()
    private val transactionViewModel: TransactionViewModel by activityViewModels()
    private val categoryViewModel: CategoryViewModel by activityViewModels()
    private lateinit var transactionAdapter: BudgetTransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionAdapter =  BudgetTransactionAdapter(categoryViewModel)
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = transactionAdapter

        val budgetId = arguments?.getString("budgetId") ?: return
        val budget = budgetViewModel.getBudgetById(budgetId)
        val category = budget?.let { categoryViewModel.getCategoryById(it.categoryID) }
        category?.let {
            binding.ivTransactionIcon.setImageResource(getIconResource(it.iconId))
            binding.tvTransactionTitle.text = it.name
            binding.tvPaidAmount.text = budget.currentAmount.toString() + "VNĐ"
            binding.tvPaidAmount.setTextColor(Color.parseColor(if (it.type == 0) "#FD3C4A" else "#00A86B"))
            binding.tvAmount.text = budget.targetAmount.toString() + "VNĐ"
            binding.tvAmount.setTextColor(Color.parseColor(if (it.type != 0) "#FD3C4A" else "#00A86B"))
            val percentage = (budget.currentAmount / budget.targetAmount * 100).toInt()
            binding.circularProgressView.setProgress(percentage)
            if (percentage >= 100) {
                binding.budgetStatus.visibility = View.VISIBLE
                binding.budgetStatus.setTextColor(Color.parseColor(if (it.type != 0) "#FD3C4A" else "#00A86B"))
                binding.budgetStatus.text = if (it.type != 0) "Quá hạn mức" else "Đạt mục tiêu"
                binding.circularProgressView.visibility = View.INVISIBLE
            } else {
                binding.budgetStatus.visibility = View.INVISIBLE
                binding.circularProgressView.visibility = View.VISIBLE
            }
        }


        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale("vi", "VN"))
        binding.tvDateEnd.text = budget?.dateEnd?.let { sdf.format(it) }
        binding.tvDateStart.text = budget?.dateStart?.let { sdf.format(it) }

        budget?.let {
            binding.budget = budget

            transactionViewModel.transactions.observe(viewLifecycleOwner) { transactions ->
                val filteredTransactions = transactions.filter { transaction ->
                    transaction.categoryID == budget.categoryID &&
                            transaction.date.toDate().after(budget.dateStart) &&
                            transaction.date.toDate().before(budget.dateEnd)
                }
                transactionAdapter.submitList(filteredTransactions)
            }
        }

        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
            Log.d("budget", "onViewCreated: ")
        }

        binding.btTrash.setOnClickListener {
            budgetId.let { id ->
                budgetViewModel.deleteBudget(id)
                findNavController().navigateUp()
            }
        }
    }
    private fun getIconResource(iconId: Int): Int {
        val iconName = "ic_item_$iconId"
        return binding.root.context.resources.getIdentifier(iconName, "drawable", binding.root.context.packageName)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
