package com.flo.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flo.app.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val userName: String = "",
    val monthlyIncome: String = "",
    val monthlyBudget: String = "",
    val currencySymbol: String = "₹",
    val isDarkTheme: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val state: StateFlow<SettingsUiState> = combine(
        userPreferences.userName,
        userPreferences.monthlyIncome,
        userPreferences.monthlyBudget,
        userPreferences.isDarkTheme,
        userPreferences.currencySymbol
    ) { name, income, budget, darkTheme, currency ->
        SettingsUiState(
            userName = name,
            monthlyIncome = if (income > 0) income.toLong().toString() else "",
            monthlyBudget = if (budget > 0) budget.toLong().toString() else "",
            isDarkTheme = darkTheme,
            currencySymbol = currency
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch { userPreferences.setDarkTheme(isDark) }
    }

    fun saveName(name: String) {
        viewModelScope.launch { userPreferences.saveUserName(name) }
    }

    fun saveIncome(income: String) {
        viewModelScope.launch {
            userPreferences.saveMonthlyIncome(income.toDoubleOrNull() ?: 0.0)
        }
    }

    fun saveBudget(budget: String) {
        viewModelScope.launch {
            userPreferences.saveMonthlyBudget(budget.toDoubleOrNull() ?: 0.0)
        }
    }
}