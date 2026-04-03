package com.flo.app.data.model

import java.time.LocalDate

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val note: String,
    val date: LocalDate
)

enum class TransactionType {
    INCOME, EXPENSE
}

enum class TransactionCategory(val label: String, val emoji: String) {
    FOOD("Food", "🍔"),
    TRANSPORT("Transport", "🚗"),
    SHOPPING("Shopping", "🛍️"),
    ENTERTAINMENT("Entertainment", "🎬"),
    HEALTH("Health", "💊"),
    EDUCATION("Education", "📚"),
    BILLS("Bills", "🧾"),
    SALARY("Salary", "💼"),
    FREELANCE("Freelance", "💻"),
    INVESTMENT("Investment", "📈"),
    OTHER("Other", "📦")
}