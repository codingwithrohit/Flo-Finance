package com.flo.app.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flo.app.data.model.Transaction
import com.flo.app.data.model.TransactionType
import com.flo.app.ui.components.FloCard
import com.flo.app.ui.theme.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    onAddTransaction: () -> Unit = {},
    onSeeAllTransactions: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scoreTip = when {
        state.healthScore.streakScore < 8   -> "💡 Log daily to boost your streak"
        state.healthScore.budgetScore < 12  -> "💡 Reduce spending to improve budget score"
        state.healthScore.savingsScore < 10 -> "💡 Save more of your income"
        state.healthScore.score < 50        -> "💡 Add transactions to get a full picture"
        else                                -> "✅ You're on track this month"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Primary
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Greeting
                GreetingHeader(
                    userName = state.userName,
                    onSettingsClick = onSettingsClick
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Health Score Ring
                HealthScoreRing(
                    score = state.healthScore.score,
                    label = state.healthScore.label,
                    tip = scoreTip,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Summary Cards Row
                SummaryCardsRow(
                    balance = state.summary.balance,
                    income = state.summary.totalIncome,
                    expense = state.summary.totalExpense
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Weekly Spending Chart
                WeeklySpendingCard(weeklyData = state.weeklySpending)

                Spacer(modifier = Modifier.height(20.dp))

                // Goal Progress
                state.goal?.let { goal ->
                    GoalProgressCard(
                        goalTitle = goal.title,
                        targetAmount = goal.targetAmount,
                        currentAmount = state.summary.balance,
                        progress = state.goalProgress
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Recent Transactions
                RecentTransactionsCard(
                    transactions = state.recentTransactions,
                    onSeeAll = onSeeAllTransactions
                )

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // FAB
        FloatingActionButton(
            onClick = onAddTransaction,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Primary,
            contentColor = Color.Black,
            shape = CircleShape
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Add Transaction")
        }
    }
}

@Composable
private fun GreetingHeader(userName: String, onSettingsClick: () -> Unit) {
    val hour = LocalTime.now().hour
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else      -> "Good evening"
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "$greeting,",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = userName.ifBlank { "there" } + " 👋",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        IconButton(onClick = onSettingsClick) {
            Icon(
                Icons.Rounded.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HealthScoreRing(
    score: Int,
    label: String,
    tip: String,
    modifier: Modifier = Modifier
) {
    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 1200, easing = EaseOutCubic),
        label = "health_score"
    )

    val sweepAngle = (animatedScore / 100f) * 270f

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 16.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f
            )
            val arcSize = Size(diameter, diameter)

            // Background arc
            drawArc(
                color = SurfaceVariant,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Score arc
            if (sweepAngle > 0f) {
                drawArc(
                    color = when {
                        score >= 80 -> Income
                        score >= 60 -> Primary
                        score >= 40 -> Color(0xFFFFB347)
                        else        -> Expense
                    },
                    startAngle = 135f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$animatedScore",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = Primary,
                letterSpacing = 2.sp
            )
            Text(
                text = "FINANCE SCORE",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tip,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun SummaryCardsRow(
    balance: Double,
    income: Double,
    expense: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            label = "Balance",
            amount = balance,
            color = if (balance >= 0) Income else Expense,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            label = "Income",
            amount = income,
            color = Income,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            label = "Expenses",
            amount = expense,
            color = Expense,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryCard(
    label: String,
    amount: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    FloCard(modifier = modifier, cornerRadius = 16.dp) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "₹${formatAmount(amount)}",
            style = MaterialTheme.typography.titleMedium,
            color = color,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun WeeklySpendingCard(weeklyData: List<Pair<String, Double>>) {
    FloCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Weekly Spending",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        val totalWeekly = weeklyData.sumOf { it.second }
        Text(
            text = "₹${formatAmount(totalWeekly)} this week",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        WeeklyBarChart(data = weeklyData)
    }
}

@Composable
private fun WeeklyBarChart(data: List<Pair<String, Double>>) {
    val maxAmount = data.maxOfOrNull { it.second } ?: 1.0
    val today = LocalDate.now().dayOfWeek.name.take(3)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { (day, amount) ->
            val isToday = day == today
            val heightFraction = if (maxAmount > 0) (amount / maxAmount).toFloat() else 0f
            val animatedFraction by animateFloatAsState(
                targetValue = heightFraction,
                animationSpec = tween(800, easing = EaseOutCubic),
                label = "bar_$day"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(animatedFraction.coerceAtLeast(0.03f))
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(
                                if (isToday) Primary
                                else Primary.copy(alpha = 0.35f)
                            )
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isToday) Primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun GoalProgressCard(
    goalTitle: String,
    targetAmount: Double,
    currentAmount: Double,
    progress: Float
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "goal_progress"
    )

    FloCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = goalTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "₹${formatAmount(currentAmount)} of ₹${formatAmount(targetAmount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                color = Primary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Primary,
            trackColor = SurfaceVariant
        )
    }
}

@Composable
private fun RecentTransactionsCard(
    transactions: List<Transaction>,
    onSeeAll: () -> Unit
) {
    FloCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = onSeeAll) {
                Text(
                    "See all",
                    color = Primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        if (transactions.isEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🧾", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "No transactions yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Tap + to add your first one",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            transactions.forEach { transaction ->
                Spacer(modifier = Modifier.height(12.dp))
                TransactionRow(transaction = transaction)
            }
        }
    }
}

@Composable
private fun TransactionRow(transaction: Transaction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (transaction.type == TransactionType.INCOME)
                            Income.copy(alpha = 0.15f)
                        else Expense.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getCategoryEmoji(transaction.category),
                    fontSize = 18.sp
                )
            }
            Column {
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = transaction.date.format(
                        DateTimeFormatter.ofPattern("MMM d")
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}₹${formatAmount(transaction.amount)}",
            style = MaterialTheme.typography.titleMedium,
            color = if (transaction.type == TransactionType.INCOME) Income else Expense,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// Helpers
private fun formatAmount(amount: Double): String {
    return if (amount >= 1000) {
        String.format("%.1fk", amount / 1000)
    } else {
        String.format("%.0f", amount)
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

@Composable
private fun GoalProgressCard(goalTitle: String, targetAmount: Double, currentAmount: Double, progress: Double) {
    GoalProgressCard(goalTitle, targetAmount, currentAmount, progress.toFloat())
}