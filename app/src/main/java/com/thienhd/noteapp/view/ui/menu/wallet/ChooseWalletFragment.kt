package com.thienhd.noteapp.view.ui.menu.wallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.thienhd.noteapp.R
import com.thienhd.noteapp.databinding.FragmentChooseWalletBinding
import com.thienhd.noteapp.view.ui.menu.wallet.adapter.WalletAdapter
import com.thienhd.noteapp.viewmodel.ChooseWalletViewModel
import com.thienhd.noteapp.viewmodel.WalletViewModel

class ChooseWalletFragment : Fragment() {

    lateinit var binding: FragmentChooseWalletBinding
    private val viewModel: WalletViewModel by activityViewModels()
    private val chooseWalletViewModel: ChooseWalletViewModel by activityViewModels()
    private lateinit var walletAdapter: WalletAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadWalletsFromFirestore()
        walletAdapter = WalletAdapter(emptyList()) { wallet ->
            chooseWalletViewModel.setWalletId(wallet.walletID)
            findNavController().navigateUp()
        }
        binding.rvWallets.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = walletAdapter
        }

        viewModel.wallets.observe(viewLifecycleOwner) { wallets ->
            walletAdapter.submitList(wallets.filter { wallet -> !wallet.isDeleted })
        }
    }
}
