package com.flo.app.ui.screens.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flo.app.data.model.CategorySpending
import com.flo.app.data.model.Insight
import com.flo.app.data.model.InsightType
import com.flo.app.ui.components.DonutChart
import com.flo.app.ui.components.FloCard
import com.flo.app.ui.components.SpendingLineChart
import com.flo.app.ui.theme.*

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Insights",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Your spending patterns this month",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }
            state.isEmpty -> {
                EmptyInsightsState()
            }
            else -> {
                state.insights.forEach { insight ->
                    InsightCard(insight = insight)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // In InsightsScreen composable, add this after insights list
                val spendingTrend by viewModel.spendingTrend.collectAsState()

                if (spendingTrend.size >= 3) {
                    Spacer(modifier = Modifier.height(16.dp))
                    FloCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "30-Day Spending Trend",
                            style = MaterialTheme.typography.labelLarge,
                            color = Primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your daily expenses this month",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SpendingLineChart(
                            data = spendingTrend,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun InsightCard(insight: Insight) {
    FloCard(modifier = Modifier.fillMaxWidth()) {
        when (insight.type) {
            InsightType.TOP_CATEGORY -> TopCategoryInsight(insight)
            InsightType.MONTHLY_COMPARISON -> MonthlyComparisonInsight(insight)
            InsightType.CATEGORY_BREAKDOWN -> CategoryBreakdownInsight(insight)
        }
    }
}

@Composable
private fun TopCategoryInsight(insight: Insight) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = insight.title,
                style = MaterialTheme.typography.labelLarge,
                color = Primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = insight.description,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "₹${insight.amount.toLong()}",
                style = MaterialTheme.typography.headlineMedium,
                color = Expense,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MonthlyComparisonInsight(insight: Insight) {
    val isMore = insight.description.contains("more")
    Column {
        Text(
            text = insight.title,
            style = MaterialTheme.typography.labelLarge,
            color = Primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = insight.description,
            style = MaterialTheme.typography.titleMedium,
            color = if (isMore) Expense else Income
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This month total: ₹${insight.amount.toLong()}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CategoryBreakdownInsight(insight: Insight) {
    Column {
        Text(
            text = insight.title,
            style = MaterialTheme.typography.labelLarge,
            color = Primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = insight.description,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Donut chart
        DonutChart(
            categories = insight.categoryBreakdown,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Bar breakdown below
        insight.categoryBreakdown.take(5).forEach { category ->
            CategoryBar(category = category)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun CategoryBar(category: CategorySpending) {
    val animatedWidth by androidx.compose.animation.core.animateFloatAsState(
        targetValue = (category.percentage / 100f).toFloat(),
        animationSpec = androidx.compose.animation.core.tween(800),
        label = "bar"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category.category,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "${category.percentage.toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "₹${category.amount.toLong()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Expense,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(SurfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(Primary)
            )
        }
    }
}

@Composable
private fun EmptyInsightsState() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📊", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Not enough data yet",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add a few transactions and\nyour insights will appear here",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }

}