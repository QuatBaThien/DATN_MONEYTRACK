package com.thienhd.noteapp.view.ui.budget.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thienhd.noteapp.data.entities.Budget
import com.thienhd.noteapp.databinding.ItemBudgetBinding
import com.thienhd.noteapp.viewmodel.CategoryViewModel

class BudgetAdapter(
    private val categoryViewModel: CategoryViewModel
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
            val category = categoryViewModel.getCategoryById(budget.categoryID)
            binding.tvBudgetCategory.text = category?.name ?: "Unknown Category"
            binding.budget = budget
            binding.executePendingBindings()
        }
    }
}
