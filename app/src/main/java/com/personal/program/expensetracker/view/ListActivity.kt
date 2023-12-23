package com.personal.program.expensetracker.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.personal.program.expensetracker.R
import com.personal.program.expensetracker.repository.TransactionRepository

class ListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private val repository = TransactionRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        recyclerView = findViewById(R.id.rvTransaction)
        recyclerView.layoutManager = LinearLayoutManager(this)

        repository.getAllTransactions { transactions ->
            adapter = TransactionAdapter(transactions)
            recyclerView.adapter = adapter
        }
    }
}

