package com.thienhd.noteapp.view.ui.menu.wallet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thienhd.noteapp.data.entities.Wallet
import com.thienhd.noteapp.databinding.ItemWalletBinding
import java.text.NumberFormat
import java.util.Locale

class WalletAdapter(
    private var wallets: List<Wallet>,
    private val itemClickListener: (Wallet) -> Unit
) : RecyclerView.Adapter<WalletAdapter.WalletViewHolder>() {
    class WalletViewHolder(private val binding: ItemWalletBinding) : RecyclerView.ViewHolder(binding.root) {
        val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        fun bind(wallet: Wallet, clickListener: (Wallet) -> Unit) {
            binding.wallet = wallet
            binding.tvWalletBalance.text = numberFormat.format(wallet.balance) + " VNĐ"
            binding.root.setOnClickListener { clickListener(wallet) }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        val binding = ItemWalletBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WalletViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        holder.bind(wallets[position], itemClickListener)
    }

    override fun getItemCount(): Int = wallets.size

    fun submitList(newWallets: List<Wallet>) {
        wallets = newWallets
        notifyDataSetChanged()
    }
}
