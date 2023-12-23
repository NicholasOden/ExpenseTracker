package com.personal.program.expensetracker.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.personal.program.expensetracker.R
import com.personal.program.expensetracker.model.Transaction
import com.personal.program.expensetracker.utils.DatePickerHelper
import com.personal.program.expensetracker.viewmodel.EntryViewModel

class EntryActivity : AppCompatActivity() {

    private lateinit var viewModel: EntryViewModel
    private lateinit var spinnerCategory: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        viewModel = ViewModelProvider(this)[EntryViewModel::class.java]

        val etAmount = findViewById<EditText>(R.id.etAmount)
        val etDate = findViewById<EditText>(R.id.etDate)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val rgType = findViewById<RadioGroup>(R.id.rgType)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        spinnerCategory = findViewById(R.id.spinnerCategory)
        rgType.setOnCheckedChangeListener { _, checkedId ->
            setupCategorySpinner(checkedId)
        }

        setupCategorySpinner(rgType.checkedRadioButtonId)

        etDate.setOnClickListener {
            DatePickerHelper().showDatePicker(this) { selectedDate ->
                etDate.setText(selectedDate)
            }
        }

        btnSubmit.setOnClickListener {
            val amount = etAmount.text.toString().toDoubleOrNull()
            val category = spinnerCategory.selectedItem.toString()
            val date = etDate.text.toString()
            val description = etDescription.text.toString()
            val type = when (rgType.checkedRadioButtonId) {
                R.id.rbIncome -> "Income"
                R.id.rbExpense -> "Expense"
                else -> ""
            }

            if (validateInput(amount, category, date, type)) {
                val transaction = Transaction("", amount!!, type, category, date, description)
                viewModel.addTransaction(transaction)
            } else {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.transactionAdded.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Transaction added successfully", Toast.LENGTH_SHORT).show()
                finish() // Close the activity
            } else {
                Toast.makeText(this, "Failed to add transaction", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCategorySpinner(transactionType: Int) {
        val categories = when (transactionType) {
            R.id.rbIncome -> arrayOf("Salary", "Bonus", "Others")
            R.id.rbExpense -> arrayOf("F&B", "Transport", "Bill & Utilities", "Entertainment")
            else -> arrayOf()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = adapter
    }

    private fun validateInput(amount: Double?, category: String, date: String, type: String): Boolean {
        return amount != null && category.isNotBlank() && date.isNotBlank() && type.isNotBlank()
    }

}
