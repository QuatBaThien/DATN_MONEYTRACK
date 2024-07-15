package com.thienhd.noteapp.view.ui.menu.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.thienhd.noteapp.databinding.FragmentWalletDetailBinding
import com.thienhd.noteapp.data.entities.Wallet
import com.thienhd.noteapp.viewmodel.WalletViewModel
import java.text.NumberFormat
import java.util.Locale

class WalletDetailFragment : Fragment() {

    private var _binding: FragmentWalletDetailBinding? = null
    private val binding get() = _binding!!
    private val walletViewModel: WalletViewModel by activityViewModels()
    private var walletID: String? = null
    private lateinit var wallet: Wallet
    val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWalletDetailBinding.inflate(inflater, container, false)
        binding.walletViewModel = walletViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        walletID = arguments?.getString("walletID")
        if (walletID == null) {
            findNavController().navigateUp()
            return
        }

        wallet = walletViewModel.getWalletByWalletID(walletID!!) ?: Wallet(0.0, false, "", "", "")
        populateWalletDetails()

        binding.btSave.setOnClickListener {
            saveChanges()
        }

        binding.btDelete.setOnClickListener {
            hideWallet()
        }
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun populateWalletDetails() {
        binding.etWalletName.setText(wallet.name)
        binding.etWalletBalance.setText(wallet.balance.toString())
    }

    private fun saveChanges() {
        wallet.name = binding.etWalletName.text.toString()
        wallet.balance = binding.etWalletBalance.text.toString().toDoubleOrNull()!!
        walletViewModel.updateWallet(wallet.walletID, wallet)
        findNavController().navigateUp()
    }

    private fun hideWallet() {
        wallet.isDeleted = true
        walletViewModel.updateWallet(wallet.walletID, wallet)
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
