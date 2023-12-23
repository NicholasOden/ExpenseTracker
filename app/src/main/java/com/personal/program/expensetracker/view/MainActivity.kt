package com.personal.program.expensetracker.view

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.personal.program.expensetracker.R
import com.personal.program.expensetracker.utils.formatRupiah
import com.personal.program.expensetracker.viewmodel.MainViewModel

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvTotalExpenses: TextView
    private lateinit var tvBalance: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        initializeUI()
        setupObservers()
        setupListeners()
        refreshData()
    }

    private fun initializeUI() {
        tvTotalIncome = findViewById(R.id.tvTotalIncome)
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses)
        tvBalance = findViewById(R.id.tvBalance)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        pieChart = findViewById(R.id.pieChart)
        setupPieChart()
    }

    private fun setupObservers() {
        viewModel.totalIncome.observe(this) { income ->
            tvTotalIncome.text = "Total Income: ${formatRupiah(income)}"
            viewModel.totalExpense.value?.let { expense ->
                updatePieChart(income, expense)
            }
        }

        viewModel.totalExpense.observe(this) { expense ->
            tvTotalExpenses.text = "Total Expenses: ${formatRupiah(expense)}"
            viewModel.totalIncome.value?.let { income ->
                updatePieChart(income, expense)
            }
        }

        viewModel.balance.observe(this) { balance ->
            tvBalance.text = "Balance: ${formatRupiah(balance)}"
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupPieChart() {
        // Configure pie chart options
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 30f
        pieChart.transparentCircleRadius = 40f
        pieChart.setUsePercentValues(true)
        pieChart.legend.isEnabled = false

    }

    private fun updatePieChart(income: Double, expense: Double) {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(income.toFloat(), "Income"))
        entries.add(PieEntry(expense.toFloat(), "Expense"))

        val dataSet = PieDataSet(entries, "Income vs Expense")

        // Set specific colors for each slice
        val colors = ArrayList<Int>()
        colors.add(Color.GREEN) // Green for Income
        colors.add(Color.RED)   // Red for Expense
        dataSet.colors = colors

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate() // refresh the chart
    }


    private fun setupListeners() {
            swipeRefreshLayout.setOnRefreshListener {
                refreshData()
            }

            findViewById<Button>(R.id.btnViewList).setOnClickListener {
                startActivity(Intent(this, ListActivity::class.java))
            }

            findViewById<Button>(R.id.btnAddEntry).setOnClickListener {
                startActivity(Intent(this, EntryActivity::class.java))
            }
        }

        private fun refreshData() {
            viewModel.fetchTotals()
            viewModel.fetchCurrentBalance()
        }

        override fun onResume() {
            super.onResume()
            refreshData()
        }
}

