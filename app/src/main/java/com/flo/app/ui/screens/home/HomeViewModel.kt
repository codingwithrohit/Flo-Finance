package com.flo.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flo.app.data.local.UserPreferences
import com.flo.app.data.model.FinancialSummary
import com.flo.app.data.model.HealthScore
import com.flo.app.data.model.Transaction
import com.flo.app.data.model.Goal
import com.flo.app.data.repository.GoalRepository
import com.flo.app.data.repository.TransactionRepository
import com.flo.app.domain.usecase.CalculateHealthScoreUseCase
import com.flo.app.domain.usecase.GetFinancialSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

data class HomeUiState(
    val userName: String = "",
    val summary: FinancialSummary = FinancialSummary(0.0, 0.0, 0.0, 0.0),
    val healthScore: HealthScore = HealthScore(0, 0, 0, 0, 0, ""),
    val recentTransactions: List<Transaction> = emptyList(),
    val weeklySpending: List<Pair<String, Double>> = emptyList(),
    val goal: Goal? = null,
    val goalProgress: Double = 0.0,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository,
    private val getFinancialSummaryUseCase: GetFinancialSummaryUseCase,
    private val calculateHealthScoreUseCase: CalculateHealthScoreUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        userPreferences.userName,
        getFinancialSummaryUseCase(LocalDate.now().withDayOfMonth(1)),
        calculateHealthScoreUseCase(),
        transactionRepository.getAllTransactions(),
        goalRepository.getActiveGoal()
    ) { userName, summary, healthScore, allTransactions, goal ->

        val recentTransactions = allTransactions.take(5)

        // Weekly spending — last 7 days grouped by day
        val today = LocalDate.now()
        val weeklySpending = (6 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            val dayLabel = date.dayOfWeek.name.take(3)
            val amount = allTransactions
                .filter {
                    it.date == date &&
                            it.type.name == "EXPENSE"
                }
                .sumOf { it.amount }
            Pair(dayLabel, amount)
        }

        // Goal progress
        val goalProgress = if (goal != null && goal.targetAmount > 0) {
            (summary.balance / goal.targetAmount).coerceIn(0.0, 1.0)
        } else 0.0

        // Add goal score to health score
        val goalBonus = when {
            goalProgress >= 1.0 -> 10
            goalProgress >= 0.7 -> 8
            goalProgress >= 0.4 -> 5
            goalProgress > 0.0  -> 2
            else                -> 0
        }
        val finalScore = healthScore.copy(
            score = (healthScore.score + goalBonus).coerceAtMost(100)
        )

        HomeUiState(
            userName = userName,
            summary = summary,
            healthScore = finalScore,
            recentTransactions = recentTransactions,
            weeklySpending = weeklySpending,
            goal = goal,
            goalProgress = goalProgress,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )
}