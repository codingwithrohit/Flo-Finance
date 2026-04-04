package com.flo.app.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flo.app.data.local.UserPreferences
import com.flo.app.data.model.Goal
import com.flo.app.data.repository.GoalRepository
import com.flo.app.data.repository.TransactionRepository
import com.flo.app.domain.usecase.GetFinancialSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class GoalsUiState(
    val goal: Goal? = null,
    val currentSavings: Double = 0.0,
    val progress: Double = 0.0,
    val streakDays: Int = 0,
    val noSpendDaysThisWeek: Int = 0,
    val showEditGoalSheet: Boolean = false,
    val formGoalTitle: String = "",
    val formGoalAmount: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val getFinancialSummaryUseCase: GetFinancialSummaryUseCase,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val state: StateFlow<GoalsUiState> = combine(
        goalRepository.getActiveGoal(),
        getFinancialSummaryUseCase(LocalDate.now().withDayOfMonth(1)),
        transactionRepository.getDistinctDaysSince(LocalDate.now().minusDays(6))
    ) { goal, summary, activeDays ->

        val progress = if (goal != null && goal.targetAmount > 0)
            (summary.balance / goal.targetAmount).coerceIn(0.0, 1.0)
        else 0.0

        val today = LocalDate.now()
        val noSpendDays = (0..6).count { daysAgo ->
            val day = today.minusDays(daysAgo.toLong()).toEpochDay()
            day !in activeDays
        }

        GoalsUiState(
            goal = goal,
            currentSavings = summary.balance,
            progress = progress,
            streakDays = activeDays.size,
            noSpendDaysThisWeek = noSpendDays,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GoalsUiState(isLoading = true)
    )

    fun openEditSheet() {
        val current = state.value.goal
        _editTitle.value = current?.title ?: ""
        _editAmount.value = current?.targetAmount?.toString() ?: ""
        _showSheet.value = true
    }

    private val _showSheet = MutableStateFlow(false)
    private val _editTitle = MutableStateFlow("")
    private val _editAmount = MutableStateFlow("")

    fun closeSheet() { _showSheet.value = false }
    fun onTitleChange(v: String) { _editTitle.value = v }
    fun onAmountChange(v: String) { _editAmount.value = v }

    fun saveGoal() {
        val amount = _editAmount.value.toDoubleOrNull() ?: return
        viewModelScope.launch {
            goalRepository.saveGoal(
                Goal(
                    targetAmount = amount,
                    title = _editTitle.value.ifBlank { "Monthly Savings" },
                    createdDate = LocalDate.now()
                )
            )
            closeSheet()
        }
    }

    fun deleteGoal() {
        viewModelScope.launch { goalRepository.deleteGoal() }
    }

    val showSheet = _showSheet.asStateFlow()
    val editTitle = _editTitle.asStateFlow()
    val editAmount = _editAmount.asStateFlow()
}