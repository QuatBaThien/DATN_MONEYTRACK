package com.thienhd.noteapp.view.ui.menu.wallet

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.thienhd.noteapp.R
import com.thienhd.noteapp.data.entities.Wallet
import com.thienhd.noteapp.databinding.FragmentWalletBinding
import com.thienhd.noteapp.view.ui.menu.wallet.adapter.WalletAdapter
import com.thienhd.noteapp.viewmodel.WalletViewModel

class WalletFragment : Fragment() {
    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WalletViewModel by activityViewModels()
    private lateinit var walletAdapter: WalletAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadWalletsFromFirestore()
        walletAdapter = WalletAdapter(emptyList()) { wallet ->
            val bundle = Bundle().apply {
                putString("walletID", wallet.walletID) // Pass walletID to detail fragment
            }
            findNavController().navigate(R.id.action_walletFragment_to_walletDetailFragment, bundle)
        }

        binding.rvWallets.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = walletAdapter
        }

        viewModel.wallets.observe(viewLifecycleOwner) { wallets ->
            walletAdapter.submitList(wallets.filter { wallet -> !wallet.isDeleted })
        }

        binding.btCreate.setOnClickListener {
            showCreateWalletDialog()
        }

        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun showCreateWalletDialog() {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_create_frist_wallet, null)

        val walletNameEditText = dialogView.findViewById<EditText>(R.id.editTextWalletName)
        val walletBalanceEditText = dialogView.findViewById<EditText>(R.id.editTextWalletBalance)
        var walletCreateTextView = dialogView.findViewById<TextView>(R.id.tv_create_wallet)
        walletCreateTextView.text = "Tạo ví tiền mới"

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<View>(R.id.buttonCreateWallet).setOnClickListener {
            val walletName = walletNameEditText.text.toString()
            val walletBalance = walletBalanceEditText.text.toString()

            if (walletName.isNotEmpty()) {
                viewModel.addWallet(
                    Wallet(
                        balance = walletBalance,
                        name = walletName,
                        isDeleted = false,
                    )
                )
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập tên ví", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
