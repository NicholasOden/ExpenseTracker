package com.personal.program.expensetracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.personal.program.expensetracker.model.Transaction
import com.personal.program.expensetracker.repository.TransactionRepository

class EntryViewModel : ViewModel() {

    private val repository = TransactionRepository()
    private val _transactionAdded = MutableLiveData<Boolean>()
    val transactionAdded: LiveData<Boolean> = _transactionAdded

    fun addTransaction(transaction: Transaction) {
        repository.addTransaction(transaction, {
            // Update the balance after successfully adding the transaction
            repository.updateBalance(transaction)
            _transactionAdded.postValue(true)
        }, {
            _transactionAdded.postValue(false)
        })
    }
}
