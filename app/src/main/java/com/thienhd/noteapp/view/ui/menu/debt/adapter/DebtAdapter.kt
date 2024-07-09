package com.thienhd.noteapp.view.ui.menu.debt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.thienhd.noteapp.data.entities.Debt
import com.thienhd.noteapp.data.entities.Loan
import com.thienhd.noteapp.databinding.ItemDebtBinding
import com.thienhd.noteapp.view.ui.menu.debt.DebtFragmentDirections
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class DebtAdapter : RecyclerView.Adapter<DebtAdapter.DebtViewHolder>() {

    private var debts: List<Any> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder {
        val binding = ItemDebtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DebtViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DebtViewHolder, position: Int) {
        holder.bind(debts[position])
    }

    override fun getItemCount(): Int = debts.size

    fun submitList(debts: List<Any>) {
        this.debts = debts
        notifyDataSetChanged()
    }

    inner class DebtViewHolder(private val binding: ItemDebtBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(debt: Any) {
            val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("vi", "VN"))
            val dateFormat1 = SimpleDateFormat("dd-MM-yyyy", Locale("vi", "VN"))
            val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))

            when (debt) {
                is Debt -> {
                    binding.tvDebtType.text = "Vay từ:"
                    binding.tvLeaderName.text = debt.lender
                    binding.tvAmount.text = numberFormat.format(debt.amount) + " VNĐ"
                    binding.tvPaidAmountText.text = "Đã trả:"
                    binding.tvPaidAmount.text = numberFormat.format(debt.paidAmount) + " VNĐ"
                    binding.tvDate.text = dateFormat.format(debt.dateBorrowed.toDate())
                    binding.tvDeadlineDate.text = if (debt.isUndeadline == 0) dateFormat1.format(debt.dateDue.toDate()) else "Không thời hạn"

                    if (debt.amount == debt.paidAmount) {
                        binding.btDoneDebt.isEnabled = false
                        binding.btDoneDebt.text = "Đã trả"
                        binding.btDelete.visibility = View.VISIBLE
                    } else {
                        binding.btDoneDebt.isEnabled = true
                        binding.btDoneDebt.text = "Trả nợ"
                        binding.btDelete.visibility = View.INVISIBLE
                    }
                    binding.btDoneDebt.setOnClickListener {
                        val action = DebtFragmentDirections.actionDebtFragmentToCreatePaymentFragment(debtId = debt.debtID, debtType = "debt")
                        it.findNavController().navigate(action)
                    }
                }
                is Loan -> {
                    binding.tvDebtType.text = "Cho:"
                    binding.tvLeaderName.text = debt.borrower + " vay"
                    binding.tvAmount.text = numberFormat.format(debt.amount) + " VNĐ"
                    binding.tvPaidAmountText.text = "Đã nhận:"
                    binding.tvPaidAmount.text = numberFormat.format(debt.paidAmount) + " VNĐ"
                    binding.tvDate.text = dateFormat.format(debt.dateBorrowed.toDate())
                    binding.tvDeadlineDate.text = if (debt.isUndeadline == 0) dateFormat1.format(debt.dateDue.toDate()) else "Không thời hạn"

                    if (debt.amount == debt.paidAmount) {
                        binding.btDoneDebt.isEnabled = false
                        binding.btDoneDebt.text = "Đã nhận"
                        binding.btDelete.visibility = View.VISIBLE
                    } else {
                        binding.btDoneDebt.isEnabled = true
                        binding.btDoneDebt.text = "Nhận lại"
                        binding.btDelete.visibility = View.INVISIBLE
                    }
                    binding.btDoneDebt.setOnClickListener {
                        val action = DebtFragmentDirections.actionDebtFragmentToCreatePaymentFragment(debtId = debt.loanID, debtType = "loan")
                        it.findNavController().navigate(action)
                    }
                }
            }
        }
    }
}
