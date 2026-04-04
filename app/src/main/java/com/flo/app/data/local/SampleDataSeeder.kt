package com.flo.app.data.local

import com.flo.app.data.model.Goal
import com.flo.app.data.model.Transaction
import com.flo.app.data.model.TransactionType
import com.flo.app.data.repository.GoalRepository
import com.flo.app.data.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SampleDataSeeder @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository,
    private val userPreferences: UserPreferences
) {
    suspend fun seedSampleData() {
        val today = LocalDate.now()

        val transactions = listOf(
            // This month - expenses
            Transaction(amount = 4500.0, type = TransactionType.EXPENSE, category = "Food", note = "Groceries & dining", date = today),
            Transaction(amount = 1200.0, type = TransactionType.EXPENSE, category = "Transport", note = "Uber & metro", date = today.minusDays(1)),
            Transaction(amount = 2800.0, type = TransactionType.EXPENSE, category = "Shopping", note = "Clothes & accessories", date = today.minusDays(2)),
            Transaction(amount = 800.0, type = TransactionType.EXPENSE, category = "Entertainment", note = "Movies & OTT", date = today.minusDays(2)),
            Transaction(amount = 3200.0, type = TransactionType.EXPENSE, category = "Food", note = "Restaurant with friends", date = today.minusDays(3)),
            Transaction(amount = 1500.0, type = TransactionType.EXPENSE, category = "Bills", note = "Electricity bill", date = today.minusDays(4)),
            Transaction(amount = 950.0, type = TransactionType.EXPENSE, category = "Health", note = "Pharmacy", date = today.minusDays(5)),
            Transaction(amount = 2200.0, type = TransactionType.EXPENSE, category = "Shopping", note = "Amazon order", date = today.minusDays(6)),
            Transaction(amount = 600.0, type = TransactionType.EXPENSE, category = "Transport", note = "Fuel", date = today.minusDays(7)),
            Transaction(amount = 1800.0, type = TransactionType.EXPENSE, category = "Food", note = "Weekly groceries", date = today.minusDays(8)),
            Transaction(amount = 500.0, type = TransactionType.EXPENSE, category = "Education", note = "Udemy course", date = today.minusDays(9)),
            Transaction(amount = 3500.0, type = TransactionType.EXPENSE, category = "Bills", note = "Internet & phone", date = today.minusDays(10)),
            Transaction(amount = 1100.0, type = TransactionType.EXPENSE, category = "Entertainment", note = "Concert tickets", date = today.minusDays(12)),
            Transaction(amount = 750.0, type = TransactionType.EXPENSE, category = "Health", note = "Gym membership", date = today.minusDays(14)),
            Transaction(amount = 2600.0, type = TransactionType.EXPENSE, category = "Food", note = "Dinner & takeout", date = today.minusDays(15)),

            // This month - income
            Transaction(amount = 75000.0, type = TransactionType.INCOME, category = "Salary", note = "Monthly salary", date = today.minusDays(5)),
            Transaction(amount = 12000.0, type = TransactionType.INCOME, category = "Freelance", note = "Design project", date = today.minusDays(10)),

            // Last month - for comparison insights
            Transaction(amount = 5200.0, type = TransactionType.EXPENSE, category = "Food", note = "Food expenses", date = today.minusDays(35)),
            Transaction(amount = 1800.0, type = TransactionType.EXPENSE, category = "Transport", note = "Transport", date = today.minusDays(36)),
            Transaction(amount = 3100.0, type = TransactionType.EXPENSE, category = "Shopping", note = "Shopping", date = today.minusDays(38)),
            Transaction(amount = 1200.0, type = TransactionType.EXPENSE, category = "Entertainment", note = "Entertainment", date = today.minusDays(40)),
            Transaction(amount = 1600.0, type = TransactionType.EXPENSE, category = "Bills", note = "Bills", date = today.minusDays(42)),
            Transaction(amount = 75000.0, type = TransactionType.INCOME, category = "Salary", note = "Monthly salary", date = today.minusDays(35)),
        )

        transactions.forEach { transactionRepository.insertTransaction(it) }

        goalRepository.saveGoal(
            Goal(
                targetAmount = 20000.0,
                title = "Monthly Savings",
                createdDate = today.minusDays(30)
            )
        )

        userPreferences.saveMonthlyIncome(75000.0)
        userPreferences.saveMonthlyBudget(60000.0)
    }

    suspend fun clearSampleData() {
        // Get the list once using first() instead of collect
        val transactions = transactionRepository.getAllTransactions().first()
        transactions.forEach { transactionRepository.deleteTransaction(it) }
        goalRepository.deleteGoal()
    }
}