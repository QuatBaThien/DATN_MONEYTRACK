package com.thienhd.noteapp.view.ui.budget.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thienhd.noteapp.R
import com.thienhd.noteapp.data.entities.Transaction
import com.thienhd.noteapp.databinding.ItemTransactionBinding
import com.thienhd.noteapp.viewmodel.CategoryViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class BudgetTransactionAdapter (
    private val categoryViewModel: CategoryViewModel
    ) : RecyclerView.Adapter<BudgetTransactionAdapter.TransactionViewHolder>() {

        private var transactions: List<Transaction> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
            val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return TransactionViewHolder(binding)
        }

        override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
            holder.bind(transactions[position])
        }

        override fun getItemCount(): Int = transactions.size

        fun submitList(transactions: List<Transaction>) {
            this.transactions = transactions
            notifyDataSetChanged()
        }

        inner class TransactionViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(transaction: Transaction) {
                val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))
                binding.apply {
                    val category = categoryViewModel.getCategoryById(transaction.categoryID)
                    tvTransactionTitle.text = category?.name ?: "Unknown Category"
                    tvTransactionAmount.text = numberFormat.format(transaction.amount) + " VNĐ"
                    tvTime.text = SimpleDateFormat("dd-MM-yyyy", Locale("vi", "VN")).format(transaction.date.toDate())
                    tvTransactionContent.text = transaction.note
                    category?.iconId?.let { iconId ->
                        val context = binding.root.context
                        val drawableId = context.resources.getIdentifier("ic_item_$iconId", "drawable", context.packageName)
                        if (drawableId != 0)
                            binding.ivTransactionIcon.setImageResource(drawableId)
                    }
                }
            }
        }
}