package com.flo.app.domain.usecase

import com.flo.app.data.model.CategorySpending
import com.flo.app.data.model.Insight
import com.flo.app.data.model.InsightType
import com.flo.app.data.model.TransactionType
import com.flo.app.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class GetInsightsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Insight>> {
        val today = LocalDate.now()
        val monthStart = today.withDayOfMonth(1)
        val lastMonthStart = monthStart.minusMonths(1)
        val lastMonthEnd = monthStart.minusDays(1)

        return repository.getAllTransactions().map { transactions ->
            val insights = mutableListOf<Insight>()

            val thisMonthExpenses = transactions.filter {
                it.type == TransactionType.EXPENSE &&
                        !it.date.isBefore(monthStart)
            }

            val lastMonthExpenses = transactions.filter {
                it.type == TransactionType.EXPENSE &&
                        !it.date.isBefore(lastMonthStart) &&
                        !it.date.isAfter(lastMonthEnd)
            }

            // Insight 1 — Top spending category
            val topCategory = thisMonthExpenses
                .groupBy { it.category }
                .mapValues { (_, txns) -> txns.sumOf { it.amount } }
                .maxByOrNull { it.value }

            if (topCategory != null) {
                insights.add(
                    Insight(
                        title = "Top Spending",
                        description = "You spent most on ${topCategory.key} this month",
                        amount = topCategory.value,
                        type = InsightType.TOP_CATEGORY
                    )
                )
            }

            // Insight 2 — This month vs last month
            val thisTotal = thisMonthExpenses.sumOf { it.amount }
            val lastTotal = lastMonthExpenses.sumOf { it.amount }

            if (lastTotal > 0) {
                val change = ((thisTotal - lastTotal) / lastTotal) * 100
                val direction = if (change >= 0) "more" else "less"
                insights.add(
                    Insight(
                        title = "Month Comparison",
                        description = "You're spending ${String.format("%.0f", Math.abs(change))}% $direction than last month",
                        amount = thisTotal,
                        type = InsightType.MONTHLY_COMPARISON
                    )
                )
            }

            // Insight 3 — Category breakdown
            val categoryBreakdown = thisMonthExpenses
                .groupBy { it.category }
                .map { (category, txns) ->
                    val amount = txns.sumOf { it.amount }
                    CategorySpending(
                        category = category,
                        amount = amount,
                        percentage = if (thisTotal > 0) (amount / thisTotal) * 100 else 0.0
                    )
                }
                .sortedByDescending { it.amount }

            if (categoryBreakdown.isNotEmpty()) {
                insights.add(
                    Insight(
                        title = "Spending Breakdown",
                        description = "Your expenses across ${categoryBreakdown.size} categories",
                        amount = thisTotal,
                        type = InsightType.CATEGORY_BREAKDOWN,
                        categoryBreakdown = categoryBreakdown
                    )
                )
            }

            insights
        }
    }
}