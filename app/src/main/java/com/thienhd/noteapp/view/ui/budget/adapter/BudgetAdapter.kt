package com.thienhd.noteapp.view.ui.budget.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.thienhd.noteapp.data.entities.Budget
import com.thienhd.noteapp.data.entities.Category
import com.thienhd.noteapp.databinding.ItemBudgetBinding
import com.thienhd.noteapp.viewmodel.CategoryViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class BudgetAdapter(
    private val categoryViewModel: CategoryViewModel,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    private var budgets: List<Budget> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ItemBudgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.bind(budgets[position])
    }

    override fun getItemCount(): Int = budgets.size

    fun submitList(budgets: List<Budget>) {
        this.budgets = budgets
        notifyDataSetChanged()
    }

    inner class BudgetViewHolder(private val binding: ItemBudgetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(budget: Budget) {
            binding.budget = budget

            val category = categoryViewModel.getCategoryById(budget.categoryID)
            category?.let {
                binding.ivTransactionIcon.setImageResource(getIconResource(it.iconId))
                binding.tvTransactionTitle.text = it.name
                binding.tvPaidAmount.text = budget.currentAmount.toString() + "VNĐ"
                binding.tvPaidAmount.setTextColor(Color.parseColor(if (it.type == 0) "#FD3C4A" else "#00A86B"))
                binding.tvAmount.text = budget.targetAmount.toString() + "VNĐ"
                binding.tvAmount.setTextColor(Color.parseColor(if (it.type != 0) "#FD3C4A" else "#00A86B"))
                val percentage = (budget.currentAmount/ budget.targetAmount * 100).toInt()
                binding.circularProgressView.setProgress(percentage)
                if (percentage>=100) {
                    binding.budgetStatus.visibility = View.VISIBLE
                    binding.budgetStatus.setTextColor(Color.parseColor(if (it.type != 0) "#FD3C4A" else "#00A86B"))
                    binding.budgetStatus.text = if (it.type != 0) "Quá hạn mức" else "Đạt mục tiêu"
                    binding.circularProgressView.visibility = View.INVISIBLE
                }else{
                    binding.budgetStatus.visibility = View.INVISIBLE
                    binding.circularProgressView.visibility = View.VISIBLE
                }

                binding.root.setOnClickListener {
                    onItemClicked(budget.budgetID)
                }
            }


            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale("vi", "VN"))
            binding.tvDateEnd.text = budget.dateEnd?.let { sdf.format(it) }
            binding.tvDateStart.text = budget.dateStart?.let { sdf.format(it) }
        }

        private fun getIconResource(iconId: Int): Int {
            val iconName = "ic_item_$iconId"
            return binding.root.context.resources.getIdentifier(iconName, "drawable", binding.root.context.packageName)
        }
    }
}
