package com.flo.app.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flo.app.data.model.Transaction
import com.flo.app.data.model.TransactionCategory
import com.flo.app.data.model.TransactionType
import com.flo.app.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TransactionUiState(
    val transactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: String = "All",
    val isLoading: Boolean = true,
    // Add/Edit sheet state
    val showSheet: Boolean = false,
    val editingTransaction: Transaction? = null,
    // Form fields
    val formAmount: String = "",
    val formType: TransactionType = TransactionType.EXPENSE,
    val formCategory: String = TransactionCategory.FOOD.label,
    val formNote: String = "",
    val formDate: LocalDate = LocalDate.now()
)

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllTransactions().collect { transactions ->
                _state.update {
                    it.copy(
                        transactions = transactions,
                        filteredTransactions = applyFilter(
                            transactions, it.searchQuery, it.selectedFilter
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchChange(query: String) {
        _state.update {
            it.copy(
                searchQuery = query,
                filteredTransactions = applyFilter(it.transactions, query, it.selectedFilter)
            )
        }
    }

    fun onFilterSelect(filter: String) {
        _state.update {
            it.copy(
                selectedFilter = filter,
                filteredTransactions = applyFilter(it.transactions, it.searchQuery, filter)
            )
        }
    }

    private fun applyFilter(
        transactions: List<Transaction>,
        query: String,
        filter: String
    ): List<Transaction> {
        return transactions
            .filter { txn ->
                when (filter) {
                    "Income"  -> txn.type == TransactionType.INCOME
                    "Expense" -> txn.type == TransactionType.EXPENSE
                    "All"     -> true
                    else      -> txn.category == filter
                }
            }
            .filter { txn ->
                if (query.isBlank()) true
                else txn.category.contains(query, ignoreCase = true) ||
                        txn.note.contains(query, ignoreCase = true)
            }
    }

    // Sheet controls
    fun openAddSheet() {
        _state.update {
            it.copy(
                showSheet = true,
                editingTransaction = null,
                formAmount = "",
                formType = TransactionType.EXPENSE,
                formCategory = TransactionCategory.FOOD.label,
                formNote = "",
                formDate = LocalDate.now()
            )
        }
    }

    fun openEditSheet(transaction: Transaction) {
        _state.update {
            it.copy(
                showSheet = true,
                editingTransaction = transaction,
                formAmount = transaction.amount.toString(),
                formType = transaction.type,
                formCategory = transaction.category,
                formNote = transaction.note,
                formDate = transaction.date
            )
        }
    }

    fun closeSheet() = _state.update { it.copy(showSheet = false) }

    // Form field updates
    fun onAmountChange(v: String)   = _state.update { it.copy(formAmount = v) }
    fun onTypeChange(v: TransactionType) = _state.update { it.copy(formType = v) }
    fun onCategoryChange(v: String) = _state.update { it.copy(formCategory = v) }
    fun onNoteChange(v: String)     = _state.update { it.copy(formNote = v) }
    fun onDateChange(v: LocalDate)  = _state.update { it.copy(formDate = v) }

    fun saveTransaction() {
        val s = _state.value
        val amount = s.formAmount.toDoubleOrNull() ?: return

        viewModelScope.launch {
            val transaction = Transaction(
                id = s.editingTransaction?.id ?: 0L,
                amount = amount,
                type = s.formType,
                category = s.formCategory,
                note = s.formNote,
                date = s.formDate
            )
            if (s.editingTransaction == null) {
                repository.insertTransaction(transaction)
            } else {
                repository.updateTransaction(transaction)
            }
            closeSheet()
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
}