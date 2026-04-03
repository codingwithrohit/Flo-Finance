package com.flo.app.data.model

data class FinancialSummary(
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double,
    val savingsRate: Double
)

data class DailySpending(
    val epochDay: Long,
    val amount: Double
)

data class CategorySpending(
    val category: String,
    val amount: Double,
    val percentage: Double
)