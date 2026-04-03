package com.flo.app.domain.usecase

import com.flo.app.data.model.FinancialSummary
import com.flo.app.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

class GetFinancialSummaryUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(from: LocalDate): Flow<FinancialSummary> {
        val incomeFlow = transactionRepository.getTotalIncomeSince(from)
        val expenseFlow = transactionRepository.getTotalExpenseSince(from)

        return combine(incomeFlow, expenseFlow) { income, expense ->
            val balance = income - expense
            val savingsRate = if (income > 0) (balance / income).coerceIn(0.0, 1.0) else 0.0

            FinancialSummary(
                totalIncome = income,
                totalExpense = expense,
                balance = balance,
                savingsRate = savingsRate
            )
        }
    }
}