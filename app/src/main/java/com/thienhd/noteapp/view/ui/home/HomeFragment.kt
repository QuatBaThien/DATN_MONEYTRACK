package com.thienhd.noteapp.view.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.thienhd.noteapp.R
import com.thienhd.noteapp.databinding.FragmentHomeBinding
import com.thienhd.noteapp.view.ui.home.adapter.TopCategoryAdapter
import com.thienhd.noteapp.view.ui.transaction.adapter.RVTransactionAdapter
import com.thienhd.noteapp.viewmodel.CategoryViewModel
import com.thienhd.noteapp.viewmodel.HomeViewModel
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val categoryViewModel: CategoryViewModel by activityViewModels()
    val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    private var isSelected = true
    private var sumWeek = 0.0
    private var sumMonnth = 0.0
   lateinit var adapter : TopCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TopCategoryAdapter(emptyList(),categoryViewModel)
        setupWalletSection()
        setupReportButtons()
        setupRecentTransactions()
        setupTopCategories()
        binding.tvSeeAllWallet.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_walletFragment)
        }
        binding.tvSeeAllTransaction.setOnClickListener {
            findNavController().navigate(R.id.nav_transaction)
        }
    }

    private fun setupTopCategories() {
        binding.rvMaxExpense.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMaxExpense.adapter = adapter
    }

    private fun setupWalletSection() {
        homeViewModel.walletSum.observe(viewLifecycleOwner) { sum ->
            binding.tvWalletSum.text = numberFormat.format(sum) + " VNĐ"
        }

        homeViewModel.wallet.observe(viewLifecycleOwner) { wallet ->
            binding.tvWalletTitle.text = wallet.name
            binding.tvWalletBalance.text = numberFormat.format(wallet.balance) + " VNĐ"
        }
    }

    private fun updateBarChart(barChart: BarChart, lastPeriod: Double, currentPeriod: Double, isWeekly: Boolean) {
        val entries = listOf(
            BarEntry(0f, lastPeriod.toFloat()),
            BarEntry(1f, currentPeriod.toFloat())
        )
        val dataSet = BarDataSet(entries, "Chi tiêu")
        dataSet.color = Color.RED // Set bar color

        val barData = BarData(dataSet)
        barData.barWidth = 0.3f // Adjust bar width
        barChart.data = barData

        // Customize XAxis
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value.toInt() == 0) {
                    if (isWeekly) "Tuần trước" else "Tháng trước"
                } else {
                    if (isWeekly) "Tuần này" else "Tháng này"
                }
            }
        }

        // Customize YAxis
        val leftAxis = barChart.axisLeft
        leftAxis.granularity = 1f
        leftAxis.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false

        // Hide the legend
        barChart.legend.isEnabled = false

        // Hide description label
        barChart.description.isEnabled = false

        barChart.invalidate() // Refresh chart
    }

    private fun setupRecentTransactions() {
        val adapter = RVTransactionAdapter(emptyList(), categoryViewModel)
        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentTransactions.adapter = adapter

        homeViewModel.recentTransactions.observe(viewLifecycleOwner) { transactions ->
            adapter.submitList(transactions)
        }
    }

    private fun setupReportButtons() {
        val barChart = binding.barChart

        binding.btWeekExpense.setOnClickListener {
            homeViewModel.weekExpense.observe(viewLifecycleOwner) { data ->
                updateReport(data.first, data.second)
                updateBarChart(barChart, data.first, data.second, true)
                sumWeek = data.second
            }
            binding.btWeekExpense.isEnabled = false
            binding.btMonthExpense.isEnabled = true


            homeViewModel.weekTopCategories.observe(viewLifecycleOwner) { categories ->
                adapter.submitList(categories)
            }
        }

        binding.btMonthExpense.setOnClickListener {
            homeViewModel.monthExpense.observe(viewLifecycleOwner) { data ->
                updateReport(data.first, data.second)
                updateBarChart(barChart, data.first, data.second, false)
                sumMonnth = data.second
            }
            binding.btWeekExpense.isEnabled = true
            binding.btMonthExpense.isEnabled = false

            homeViewModel.monthTopCategories.observe(viewLifecycleOwner) { categories ->
                adapter.submitList(categories)
            }
        }

        // Initialize with weekly report
        binding.btWeekExpense.performClick()
    }

    private fun updateReport(lastPeriod: Double, currentPeriod: Double) {
        val percentChange = homeViewModel.getExpenseChangePercent(lastPeriod, currentPeriod)

        binding.tvSumExpense.text = numberFormat.format(currentPeriod) + " VND"
        binding.icExpensePercent.text = "$percentChange%"
        binding.icExpensePercent.setTextColor(if (percentChange > 0) Color.RED else if (percentChange < 0) Color.GREEN else Color.YELLOW)
        binding.icExpenseStatus.setImageResource(if (percentChange > 0) R.drawable.ic_up else if (percentChange < 0) R.drawable.ic_down else R.drawable.ic_stop)
    }


    override fun onResume() {
        super.onResume()
        homeViewModel.loadExpenseReport()
        homeViewModel.loadWalletSum()
        homeViewModel.loadHighestBalanceWallet()
        homeViewModel.loadRecentTransactions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
