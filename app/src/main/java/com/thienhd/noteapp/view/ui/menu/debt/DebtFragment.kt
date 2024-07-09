package com.thienhd.noteapp.view.ui.menu.debt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.thienhd.noteapp.R
import com.thienhd.noteapp.databinding.FragmentDebtBinding
import com.thienhd.noteapp.view.ui.menu.debt.adapter.DebtAdapter
import com.thienhd.noteapp.viewmodel.DebtViewModel

class DebtFragment : Fragment() {

    private var _binding: FragmentDebtBinding? = null
    private val binding get() = _binding!!
    private val debtViewModel: DebtViewModel by activityViewModels()
    private lateinit var debtAdapter: DebtAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDebtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        debtAdapter = DebtAdapter()
        binding.rvDebts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDebts.adapter = debtAdapter

        debtViewModel.debts.observe(viewLifecycleOwner) { debts ->
            debtAdapter.submitList(debts)
        }

        debtViewModel.loans.observe(viewLifecycleOwner) { loans ->
            debtAdapter.submitList(loans)
        }
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btCreate.setOnClickListener {
            findNavController().navigate(R.id.action_debtFragment_to_createDebtFragment)
        }

        binding.btVay.setOnClickListener {
            setSelectedButton(binding.btVay)
            setUnselectedButton(binding.btChoVay)
            debtViewModel.loadDebts() // Load Debts
        }

        binding.btChoVay.setOnClickListener {
            setSelectedButton(binding.btChoVay)
            setUnselectedButton(binding.btVay)
            debtViewModel.loadLoans() // Load Loans
        }

        // Default to showing debts on fragment start
        debtViewModel.loadDebts()
        setSelectedButton(binding.btVay)
        setUnselectedButton(binding.btChoVay)
    }

    private fun setSelectedButton(button: MaterialButton) {
        button.isSelected = true
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    private fun setUnselectedButton(button: MaterialButton) {
        button.isSelected = false
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
