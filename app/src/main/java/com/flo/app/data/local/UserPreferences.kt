package com.flo.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore: DataStore<Preferences> = context.dataStore

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val MONTHLY_INCOME = doublePreferencesKey("monthly_income")
        val MONTHLY_BUDGET = doublePreferencesKey("monthly_budget")
        val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
        val KEY_SWIPE_HINT_SHOWN = booleanPreferencesKey("swipe_hint_shown")
    }

    val userName: Flow<String> = dataStore.data.map { it[USER_NAME] ?: "" }
    val monthlyIncome: Flow<Double> = dataStore.data.map { it[MONTHLY_INCOME] ?: 0.0 }
    val monthlyBudget: Flow<Double> = dataStore.data.map { it[MONTHLY_BUDGET] ?: 0.0 }
    val isOnboarded: Flow<Boolean> = dataStore.data.map { it[IS_ONBOARDED] ?: false }
    val isDarkTheme: Flow<Boolean> = dataStore.data.map { it[IS_DARK_THEME] ?: true }
    val currencySymbol: Flow<String> = dataStore.data.map { it[CURRENCY_SYMBOL] ?: "₹" }
    val swipeHintShown: Flow<Boolean> = dataStore.data.map { it[KEY_SWIPE_HINT_SHOWN] ?: false }

    suspend fun saveUserName(name: String) {
        dataStore.edit { it[USER_NAME] = name }
    }

    suspend fun saveMonthlyIncome(income: Double) {
        dataStore.edit { it[MONTHLY_INCOME] = income }
    }

    suspend fun saveMonthlyBudget(budget: Double) {
        dataStore.edit { it[MONTHLY_BUDGET] = budget }
    }

    suspend fun setOnboarded(value: Boolean) {
        dataStore.edit { it[IS_ONBOARDED] = value }
    }

    suspend fun setDarkTheme(value: Boolean) {
        dataStore.edit { it[IS_DARK_THEME] = value }
    }

    suspend fun saveCurrencySymbol(symbol: String) {
        dataStore.edit { it[CURRENCY_SYMBOL] = symbol }
    }
    suspend fun setSwipeHintShown() {
        dataStore.edit { it[KEY_SWIPE_HINT_SHOWN] = true }
    }
}