package com.personal.program.expensetracker.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.personal.program.expensetracker.R
import com.personal.program.expensetracker.model.Transaction
import com.personal.program.expensetracker.utils.formatRupiah

class TransactionAdapter(private var transactions: List<Transaction>) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAmount: TextView = view.findViewById(R.id.tvTransactionAmount)
        val tvType: TextView = view.findViewById(R.id.tvTransactionType)
        val tvCategory: TextView = view.findViewById(R.id.tvTransactionCategory)
        val tvDate: TextView = view.findViewById(R.id.tvTransactionDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.tvAmount.text = formatRupiah(transaction.amount)
        holder.tvType.text = transaction.type
        holder.tvCategory.text = transaction.category
        holder.tvDate.text = transaction.date
    }

    override fun getItemCount() = transactions.size

}

