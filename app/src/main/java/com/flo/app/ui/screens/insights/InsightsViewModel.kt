package com.flo.app.ui.screens.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flo.app.data.model.Insight
import com.flo.app.data.repository.TransactionRepository
import com.flo.app.domain.usecase.GetInsightsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

data class InsightsUiState(
    val insights: List<Insight> = emptyList(),
    val isLoading: Boolean = true,
    val isEmpty: Boolean = false
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val getInsightsUseCase: GetInsightsUseCase,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val state: StateFlow<InsightsUiState> = getInsightsUseCase()
        .map { insights ->
            InsightsUiState(
                insights = insights,
                isLoading = false,
                isEmpty = insights.isEmpty()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InsightsUiState(isLoading = true)
        )

    val spendingTrend: StateFlow<List<Pair<String, Double>>> =
        transactionRepository.getTransactionsSince(LocalDate.now().minusDays(29))
            .map { transactions ->
                (29 downTo 0).map { daysAgo ->
                    val date = LocalDate.now().minusDays(daysAgo.toLong())
                    val label = "${date.dayOfMonth}/${date.monthValue}"
                    val amount = transactions
                        .filter { it.date == date && it.type.name == "EXPENSE" }
                        .sumOf { it.amount }
                    Pair(label, amount)
                }.filter { it.second > 0 } // only days with spending
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}