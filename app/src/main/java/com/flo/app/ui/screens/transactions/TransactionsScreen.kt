package com.flo.app.ui.screens.transactions

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flo.app.data.model.Transaction
import com.flo.app.data.model.TransactionCategory
import com.flo.app.data.model.TransactionType
import com.flo.app.ui.screens.onboarding.FloButton
import com.flo.app.ui.screens.onboarding.FloTextField
import com.flo.app.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    autoOpenSheet: Boolean = false,
    onAddTransaction: () -> Unit = {},
    onEditTransaction: (Long) -> Unit = {},
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(autoOpenSheet) {
        if (autoOpenSheet) viewModel.openAddSheet()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header
            Text(
                text = "Transactions",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            SearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchChange,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter chips
            FilterChipsRow(
                selectedFilter = state.selectedFilter,
                onFilterSelect = viewModel::onFilterSelect
            )

            // Swipe hint
            val swipeHintShown by viewModel.swipeHintShown.collectAsState()
            var showHint by remember { mutableStateOf(false) }

            LaunchedEffect(swipeHintShown) {
                if (!swipeHintShown) {
                    showHint = true
                    kotlinx.coroutines.delay(3000)
                    showHint = false
                    viewModel.markSwipeHintShown()
                }
            }

            AnimatedVisibility(
                visible = showHint,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.SwipeLeft,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Swipe left on a transaction to delete",
                        style = MaterialTheme.typography.labelMedium,
                        color = Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Transaction list
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (state.filteredTransactions.isEmpty()) {
                EmptyTransactionsState(hasFilter = state.selectedFilter != "All" || state.searchQuery.isNotBlank())
            } else {
                TransactionList(
                    transactions = state.filteredTransactions,
                    onEdit = viewModel::openEditSheet,
                    onDelete = viewModel::deleteTransaction
                )
            }
        }

        // FAB
        FloatingActionButton(
            onClick = viewModel::openAddSheet,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Primary,
            contentColor = Color.Black,
            shape = CircleShape
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Add")
        }

        // Bottom Sheet
        if (state.showSheet) {
            ModalBottomSheet(
                onDismissRequest = viewModel::closeSheet,
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                TransactionForm(
                    state = state,
                    onAmountChange = viewModel::onAmountChange,
                    onTypeChange = viewModel::onTypeChange,
                    onCategoryChange = viewModel::onCategoryChange,
                    onNoteChange = viewModel::onNoteChange,
                    onSave = viewModel::saveTransaction,
                    onDismiss = viewModel::closeSheet
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text("Search transactions...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        leadingIcon = {
            Icon(Icons.Rounded.Search, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Rounded.Clear, contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
private fun FilterChipsRow(
    selectedFilter: String,
    onFilterSelect: (String) -> Unit
) {
    val filters = listOf("All", "Income", "Expense") +
            TransactionCategory.entries.map { it.label }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            val isSelected = filter == selectedFilter
            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelect(filter) },
                label = { Text(filter, style = MaterialTheme.typography.labelLarge) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary,
                    selectedLabelColor = Color.Black,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    selectedBorderColor = Primary,
                    borderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

@Composable
private fun TransactionList(
    transactions: List<Transaction>,
    onEdit: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit
) {
    // Group by date
    val grouped = transactions.groupBy { it.date }
        .toSortedMap(compareByDescending { it })

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        grouped.forEach { (date, txns) ->
            item {
                Text(
                    text = formatDateHeader(date),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(txns, key = { it.id }) { transaction ->
                SwipeToDeleteTransaction(
                    transaction = transaction,
                    onEdit = onEdit,
                    onDelete = onDelete
                )
            }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteTransaction(
    transaction: Transaction,
    onEdit: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete(transaction)
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Expense),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 20.dp)
                )
            }
        }
    ) {
        TransactionCard(
            transaction = transaction,
            onClick = { onEdit(transaction) }
        )
    }
}

@Composable
private fun TransactionCard(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (transaction.type == TransactionType.INCOME)
                                Income.copy(alpha = 0.15f)
                            else Expense.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(getCategoryEmoji(transaction.category), fontSize = 20.sp)
                }
                Column {
                    Text(
                        text = transaction.category,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (transaction.note.isNotBlank()) {
                        Text(
                            text = transaction.note,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Text(
                text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}₹${transaction.amount.toLong()}",
                style = MaterialTheme.typography.titleMedium,
                color = if (transaction.type == TransactionType.INCOME) Income else Expense,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TransactionForm(
    state: TransactionUiState,
    onAmountChange: (String) -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onCategoryChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = if (state.editingTransaction == null) "Add Transaction" else "Edit Transaction",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Income / Expense toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp)
        ) {
            listOf(TransactionType.EXPENSE, TransactionType.INCOME).forEach { type ->
                val isSelected = state.formType == type
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) {
                                if (type == TransactionType.INCOME) Income else Expense
                            } else Color.Transparent
                        )
                        .clickable { onTypeChange(type) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected) Color.White
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Amount
        FloTextField(
            value = state.formAmount,
            onValueChange = onAmountChange,
            placeholder = "Amount (₹)",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category chips
        Text(
            "Category",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        val categories = if (state.formType == TransactionType.INCOME) {
            listOf(
                TransactionCategory.SALARY,
                TransactionCategory.FREELANCE,
                TransactionCategory.INVESTMENT,
                TransactionCategory.OTHER
            )
        } else {
            TransactionCategory.entries.filter {
                it !in listOf(
                    TransactionCategory.SALARY,
                    TransactionCategory.FREELANCE,
                    TransactionCategory.INVESTMENT
                )
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { category ->
                val isSelected = state.formCategory == category.label
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategoryChange(category.label) },
                    label = {
                        Text(
                            "${category.emoji} ${category.label}",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Primary,
                        selectedLabelColor = Color.Black,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        selectedBorderColor = Primary,
                        borderColor = Color.Transparent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Note
        FloTextField(
            value = state.formNote,
            onValueChange = onNoteChange,
            placeholder = "Note (optional)"
        )

        Spacer(modifier = Modifier.height(24.dp))

        FloButton(
            text = if (state.editingTransaction == null) "Add Transaction" else "Save Changes",
            onClick = onSave,
            enabled = state.formAmount.isNotBlank()
        )
    }
}

@Composable
private fun EmptyTransactionsState(hasFilter: Boolean) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔍".takeIf { hasFilter } ?: "💸", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (hasFilter) "No matching transactions"
                else "No transactions yet",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (hasFilter) "Try a different filter or search"
                else "Tap + to add your first transaction",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatDateHeader(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today            -> "Today"
        today.minusDays(1) -> "Yesterday"
        else             -> date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
    }
}

private fun getCategoryEmoji(category: String): String {
    return when (category.lowercase()) {
        "food"          -> "🍔"
        "transport"     -> "🚗"
        "shopping"      -> "🛍️"
        "entertainment" -> "🎬"
        "health"        -> "💊"
        "education"     -> "📚"
        "bills"         -> "🧾"
        "salary"        -> "💼"
        "freelance"     -> "💻"
        "investment"    -> "📈"
        else            -> "📦"
    }
}   