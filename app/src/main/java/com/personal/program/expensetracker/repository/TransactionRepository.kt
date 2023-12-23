package com.personal.program.expensetracker.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.ValueEventListener
import com.personal.program.expensetracker.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionRepository {

    private val databaseReference = FirebaseDatabase.getInstance().getReference()

    fun addTransaction(transaction: Transaction, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        incrementAndGetTransactionCount(currentDate, { count ->
            val transactionId = "$currentDate-$count"
            val newTransaction = transaction.copy(transactionId = transactionId)
            databaseReference.child("transactions").child(transactionId).setValue(newTransaction)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it) }
        }, { exception ->
            onError(exception)
        })
    }

    private fun incrementAndGetTransactionCount(date: String, onSuccess: (Int) -> Unit, onError: (Exception) -> Unit) {
        val dailyCountRef = databaseReference.child("dailyCounts").child(date)
        dailyCountRef.runTransaction(object : com.google.firebase.database.Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): com.google.firebase.database.Transaction.Result {
                var count = mutableData.getValue(Int::class.java) ?: 0
                count++
                mutableData.value = count
                return com.google.firebase.database.Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                if (committed && dataSnapshot != null) {
                    val newCount = dataSnapshot.getValue(Int::class.java) ?: 1
                    onSuccess(newCount)
                } else {
                    onError(databaseError?.toException() ?: Exception("Unknown error occurred"))
                }
            }
        })
    }

    fun updateBalance(newTransaction: Transaction) {
        val balanceRef = databaseReference.child("balance")
        balanceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentBalance = snapshot.getValue(Double::class.java) ?: 0.0
                val updatedBalance = if (newTransaction.type == "Income") {
                    currentBalance + newTransaction.amount
                } else {
                    currentBalance - newTransaction.amount
                }
                balanceRef.setValue(updatedBalance)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    fun getCurrentBalance(onResult: (Double) -> Unit) {
        val balanceRef = databaseReference.child("balance")
        balanceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val balance = snapshot.getValue(Double::class.java) ?: 0.0
                onResult(balance)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    fun getTotalIncome(onResult: (Double) -> Unit) {
        databaseReference.child("transactions").orderByChild("type").equalTo("Income")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val total = snapshot.children.mapNotNull { it.getValue(Transaction::class.java)?.amount }.sum()
                    onResult(total)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors.
                }
            })
    }

    fun getTotalExpense(onResult: (Double) -> Unit) {
        databaseReference.child("transactions").orderByChild("type").equalTo("Expense")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val total = snapshot.children.mapNotNull { it.getValue(Transaction::class.java)?.amount }.sum()
                    onResult(total)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors.
                }
            })
    }

    fun getAllTransactions(onResult: (List<Transaction>) -> Unit) {
        val ref = databaseReference.child("transactions")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactions = mutableListOf<Transaction>()

                for (transactionSnapshot in snapshot.children) {
                    val transaction = transactionSnapshot.getValue(Transaction::class.java)
                    if (transaction != null) {
                        transactions.add(transaction)
                    }
                }
                onResult(transactions)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    // Implement other CRUD methods as needed
}
