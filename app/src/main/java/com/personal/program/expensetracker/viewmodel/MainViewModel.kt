package com.personal.program.expensetracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.personal.program.expensetracker.repository.TransactionRepository

class MainViewModel : ViewModel() {

    private val repository = TransactionRepository()

    private val _totalIncome = MutableLiveData<Double>()
    val totalIncome: LiveData<Double> = _totalIncome

    private val _totalExpense = MutableLiveData<Double>()
    val totalExpense: LiveData<Double> = _totalExpense

    private val _balance = MutableLiveData<Double>()
    val balance: LiveData<Double> = _balance

    fun fetchTotals() {
        repository.getTotalIncome { income ->
            _totalIncome.postValue(income)
        }

        repository.getTotalExpense { expense ->
            _totalExpense.postValue(expense)
        }
    }

    fun fetchCurrentBalance() {
        repository.getCurrentBalance { balance ->
            _balance.postValue(balance)
        }
    }
}



