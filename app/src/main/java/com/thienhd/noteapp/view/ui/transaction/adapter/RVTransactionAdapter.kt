package com.thienhd.noteapp.view.ui.transaction.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.thienhd.noteapp.R
import com.thienhd.noteapp.data.entities.Transaction
import com.thienhd.noteapp.databinding.ItemDayTextBinding
import com.thienhd.noteapp.databinding.ItemTransactionBinding
import com.thienhd.noteapp.viewmodel.CategoryViewModel
import java.text.SimpleDateFormat
import java.util.*

class RVTransactionAdapter(
    private var transactionList: List<Transaction>,
    private val categoryViewModel: CategoryViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.TYPE_ONE.type -> {
                val binding = ItemDayTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TypeOneViewHolder(binding)
            }
            else -> {
                val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TypeTwoViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            transactionList[position].isDaytitle -> ViewType.TYPE_ONE.type
            else -> ViewType.TYPE_TWO.type
        }
    }

    inner class TypeOneViewHolder(private val binding: ItemDayTextBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Transaction) {
            binding.tvTransactionDay.text = formatDate(item.date)
        }
    }

    inner class TypeTwoViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Transaction) {
            with(binding) {
                val category = categoryViewModel.getCategoryById(item.categoryID)
                tvTransactionContent.text = item.note
                tvTime.text = formatDate(item.date)
                tvTransactionAmount.setTextColor(Color.parseColor(if (item.type != 0) "#FD3C4A" else "#00A86B"))
                tvTransactionAmount.text = (if (item.type != 0) "- " else "+ ") + item.amount + " VNĐ"
                when (item.type){
                    3 -> {
                        tvTransactionTitle.text = "Vay tiền"
                        val iconResId = root.context.resources.getIdentifier("ic_item_debt", "drawable", root.context.packageName)
                        ivTransactionIcon.setImageResource(iconResId)
                    }
                    4 -> {
                        tvTransactionTitle.text = "Cho vay"
                        val iconResId = root.context.resources.getIdentifier("ic_item_loan", "drawable", root.context.packageName)
                        ivTransactionIcon.setImageResource(iconResId)
                    }
                    5 -> {
                        tvTransactionTitle.text = "Trả nợ"
                        val iconResId = root.context.resources.getIdentifier("ic_item_paid_debt", "drawable", root.context.packageName)
                        ivTransactionIcon.setImageResource(iconResId)
                    }
                    6 -> {
                        tvTransactionTitle.text = "Thu nợ"
                        val iconResId = root.context.resources.getIdentifier("ic_item_get_paid", "drawable", root.context.packageName)
                        ivTransactionIcon.setImageResource(iconResId)
                    }

                    else -> {
                        val iconName = "ic_item_${category?.iconId}"
                        tvTransactionTitle.text = category?.name
                        val iconResId = root.context.resources.getIdentifier(iconName, "drawable", root.context.packageName)
                        ivTransactionIcon.setImageResource(iconResId)
                    }
                }



                root.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString("transactionId", item.transactionID) // Use transactionID as a String
                    }
                    it.findNavController().navigate(R.id.action_transactionFragment_to_editTransactionFragment, bundle)
                }
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder.apply {
            when (viewHolder) {
                is TypeOneViewHolder -> viewHolder.bind(transactionList[position])
                is TypeTwoViewHolder -> viewHolder.bind(transactionList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    fun submitList(newTransactions: List<Transaction>) {
        transactionList = newTransactions
        notifyDataSetChanged()
    }

    private fun formatDate(timestamp: Timestamp): String {
        val sdf = SimpleDateFormat("HH:mm, dd 'thg' MM yyyy", Locale("vi", "VN"))
        return sdf.format(timestamp.toDate())
    }
}

enum class ViewType(val type: Int) {
    TYPE_ONE(0),
    TYPE_TWO(1)
}
