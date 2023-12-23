package com.personal.program.expensetracker.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.personal.program.expensetracker.R
import com.personal.program.expensetracker.utils.formatRupiah
import com.personal.program.expensetracker.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvTotalExpenses: TextView
    private lateinit var tvBalance: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

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
    }

    private fun setupObservers() {
        viewModel.totalIncome.observe(this) { income ->
            tvTotalIncome.text = "Total Income: ${formatRupiah(income)}"
        }

        viewModel.totalExpense.observe(this) { expense ->
            tvTotalExpenses.text = "Total Expenses: ${formatRupiah(expense)}"
        }

        viewModel.balance.observe(this) { balance ->
            tvBalance.text = "Balance: ${formatRupiah(balance)}"
            swipeRefreshLayout.isRefreshing = false
        }
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

