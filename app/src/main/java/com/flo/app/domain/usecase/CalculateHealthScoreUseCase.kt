package com.flo.app.domain.usecase

import com.flo.app.data.repository.TransactionRepository
import com.flo.app.data.local.UserPreferences
import com.flo.app.data.model.HealthScore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

class CalculateHealthScoreUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val userPreferences: UserPreferences
) {
    operator fun invoke(): Flow<HealthScore> {
        val today = LocalDate.now()
        val monthStart = today.withDayOfMonth(1)
        val weekStart = today.minusDays(6)

        val incomeFlow = transactionRepository.getTotalIncomeSince(monthStart)
        val expenseFlow = transactionRepository.getTotalExpenseSince(monthStart)
        val activeDaysFlow = transactionRepository.getDistinctDaysSince(weekStart)
        val budgetFlow = userPreferences.monthlyBudget

        return combine(
            incomeFlow,
            expenseFlow,
            activeDaysFlow,
            budgetFlow
        ) { income, expense, activeDays, budget ->

            // 1. Budget Score (30 pts)
            val budgetScore = if (budget > 0) {
                val ratio = expense / budget
                when {
                    ratio <= 0.5  -> 30.0
                    ratio <= 0.75 -> 22.0
                    ratio <= 1.0  -> 12.0
                    else          -> 0.0
                }
            } else 15.0  // no budget set → neutral score

            // 2. Streak Score (20 pts) — logged at least once in last 7 days
            val daysLogged = activeDays.size.coerceAtMost(7)
            val streakScore = (daysLogged / 7.0) * 20.0

            // 3. Savings Rate Score (25 pts)
            val savingsScore = if (income > 0) {
                val rate = (income - expense) / income
                when {
                    rate >= 0.3 -> 25.0
                    rate >= 0.2 -> 20.0
                    rate >= 0.1 -> 12.0
                    rate >= 0.0 -> 5.0
                    else        -> 0.0
                }
            } else 0.0

            // 4. No-spend days score (15 pts)
            val totalDaysInRange = 7
            val daysWithExpense = activeDays.size
            val noSpendDays = (totalDaysInRange - daysWithExpense).coerceAtLeast(0)
            val noSpendScore = (noSpendDays / 7.0) * 15.0

            // 5. Goal progress score (10 pts) — handled as bonus
            val rawScore = budgetScore + streakScore + savingsScore + noSpendScore
            val totalScore = rawScore.coerceIn(0.0, 90.0) // 90 max here, +10 from goal

            HealthScore(
                score = totalScore.toInt(),
                budgetScore = budgetScore.toInt(),
                streakScore = streakScore.toInt(),
                savingsScore = savingsScore.toInt(),
                noSpendScore = noSpendScore.toInt(),
                label = when {
                    totalScore >= 80 -> "Excellent"
                    totalScore >= 60 -> "Good"
                    totalScore >= 40 -> "Fair"
                    else             -> "Needs Work"
                }
            )
        }
    }
}