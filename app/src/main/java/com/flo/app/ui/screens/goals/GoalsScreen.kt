package com.flo.app.ui.screens.goals

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flo.app.ui.components.FloCard
import com.flo.app.ui.screens.onboarding.FloButton
import com.flo.app.ui.screens.onboarding.FloTextField
import com.flo.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val showSheet by viewModel.showSheet.collectAsState()
    val editTitle by viewModel.editTitle.collectAsState()
    val editAmount by viewModel.editAmount.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Goals & Streaks",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (state.goal != null) {
                IconButton(onClick = viewModel::openEditSheet) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Edit Goal", tint = Primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (state.goal == null) {
            NoGoalState(onSetGoal = viewModel::openEditSheet)
        } else {
            // Goal progress card
            GoalProgressCard(
                title = state.goal!!.title,
                current = state.currentSavings,
                target = state.goal!!.targetAmount,
                progress = state.progress
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Streak and no-spend row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    emoji = "🔥",
                    value = "${state.streakDays}",
                    label = "Day Streak",
                    sublabel = "days logged this week",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    emoji = "🧘",
                    value = "${state.noSpendDaysThisWeek}",
                    label = "No-Spend Days",
                    sublabel = "days without expenses",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tips card
            TipsCard(progress = state.progress, streak = state.streakDays)

            Spacer(modifier = Modifier.height(20.dp))

            // Danger zone
            OutlinedButton(
                onClick = viewModel::deleteGoal,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Expense),
                border = androidx.compose.foundation.BorderStroke(1.dp, Expense)
            ) {
                Text("Remove Goal", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = viewModel::closeSheet,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = if (state.goal == null) "Set a Goal" else "Edit Goal",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(24.dp))
                FloTextField(
                    value = editTitle,
                    onValueChange = viewModel::onTitleChange,
                    placeholder = "Goal name (e.g. Emergency Fund)"
                )
                Spacer(modifier = Modifier.height(16.dp))
                FloTextField(
                    value = editAmount,
                    onValueChange = viewModel::onAmountChange,
                    placeholder = "Target amount (₹)"
                )
                Spacer(modifier = Modifier.height(24.dp))
                FloButton(
                    text = "Save Goal",
                    onClick = viewModel::saveGoal,
                    enabled = editAmount.isNotBlank()
                )
            }
        }
    }
}

@Composable
private fun GoalProgressCard(
    title: String,
    current: Double,
    target: Double,
    progress: Double
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.toFloat(),
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "goal"
    )

    FloCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "₹${current.toLong()} saved of ₹${target.toLong()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.displayMedium,
                color = Primary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = Primary,
            trackColor = SurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = when {
                progress >= 1.0 -> "🎉 Goal achieved! Amazing work!"
                progress >= 0.7 -> "Almost there! Keep going!"
                progress >= 0.4 -> "Good progress, stay consistent!"
                progress > 0.0  -> "Every rupee counts. Keep saving!"
                else            -> "Start saving to see progress here"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatCard(
    emoji: String,
    value: String,
    label: String,
    sublabel: String,
    modifier: Modifier = Modifier
) {
    FloCard(modifier = modifier) {
        Text(emoji, fontSize = 28.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.displayMedium,
            color = Primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = sublabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TipsCard(progress: Double, streak: Int) {
    val tip = when {
        streak == 0    -> "💡 Log at least one transaction today to start your streak"
        streak < 3     -> "💡 Log for ${3 - streak} more days to build a solid habit"
        progress < 0.3 -> "💡 Try reducing one expense category this week"
        progress < 0.7 -> "💡 You're making progress — stay consistent!"
        else           -> "✅ You're crushing your goal this month!"
    }

    FloCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text("🎯", fontSize = 24.sp)
            Column {
                Text(
                    text = "Smart Tip",
                    style = MaterialTheme.typography.titleMedium,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun NoGoalState(onSetGoal: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text("🎯", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No goal set yet",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Set a savings goal to track your\nprogress and stay motivated",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        FloButton(
            text = "Set a Goal",
            onClick = onSetGoal,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}