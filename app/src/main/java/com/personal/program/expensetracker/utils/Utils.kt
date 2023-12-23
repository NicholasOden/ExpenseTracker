package com.personal.program.expensetracker.utils


import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(number: Double): String {
    val localeID = Locale("in", "ID")
    val formatter = NumberFormat.getCurrencyInstance(localeID)
    return formatter.format(number).replace(",00", "")
}

