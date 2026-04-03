package com.flo.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flo.app.data.local.UserPreferences
import com.flo.app.data.model.Goal
import com.flo.app.data.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class OnboardingState(
    val currentStep: Int = 0,
    val name: String = "",
    val monthlyIncome: String = "",
    val monthlyBudget: String = "",
    val goalTitle: String = "Monthly Savings",
    val goalAmount: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state = _state.asStateFlow()

    fun onNameChange(value: String) = _state.update { it.copy(name = value) }
    fun onIncomeChange(value: String) = _state.update { it.copy(monthlyIncome = value) }
    fun onBudgetChange(value: String) = _state.update { it.copy(monthlyBudget = value) }
    fun onGoalTitleChange(value: String) = _state.update { it.copy(goalTitle = value) }
    fun onGoalAmountChange(value: String) = _state.update { it.copy(goalAmount = value) }

    fun nextStep() = _state.update { it.copy(currentStep = it.currentStep + 1) }
    fun prevStep() = _state.update { it.copy(currentStep = it.currentStep - 1) }

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val currentState = _state.value

            userPreferences.saveUserName(currentState.name.trim())
            userPreferences.saveMonthlyIncome(
                currentState.monthlyIncome.toDoubleOrNull() ?: 0.0
            )
            userPreferences.saveMonthlyBudget(
                currentState.monthlyBudget.toDoubleOrNull()
                    ?: (currentState.monthlyIncome.toDoubleOrNull() ?: 0.0)
            )

            val goalAmount = currentState.goalAmount.toDoubleOrNull()
            if (goalAmount != null && goalAmount > 0) {
                goalRepository.saveGoal(
                    Goal(
                        targetAmount = goalAmount,
                        title = currentState.goalTitle,
                        createdDate = LocalDate.now()
                    )
                )
            }

            userPreferences.setOnboarded(true)
            onComplete()
        }
    }
}