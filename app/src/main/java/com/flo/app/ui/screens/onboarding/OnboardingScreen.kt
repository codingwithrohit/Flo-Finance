package com.flo.app.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flo.app.ui.theme.Income
import com.flo.app.ui.theme.OnSurface
import com.flo.app.ui.theme.Primary

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit = {},
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        // Step indicators
        StepIndicator(currentStep = state.currentStep, totalSteps = 3)

        Spacer(modifier = Modifier.height(56.dp))

        // Animated step content
        AnimatedContent(
            targetState = state.currentStep,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                }
            },
            label = "onboarding_step"
        ) { step ->
            when (step) {
                0 -> NameStep(
                    name = state.name,
                    onNameChange = viewModel::onNameChange,
                    onNext = viewModel::nextStep
                )
                1 -> IncomeStep(
                    income = state.monthlyIncome,
                    budget = state.monthlyBudget,
                    onIncomeChange = viewModel::onIncomeChange,
                    onBudgetChange = viewModel::onBudgetChange,
                    onNext = viewModel::nextStep,
                    onBack = viewModel::prevStep
                )
                2 -> GoalStep(
                    goalTitle = state.goalTitle,
                    goalAmount = state.goalAmount,
                    onGoalTitleChange = viewModel::onGoalTitleChange,
                    onGoalAmountChange = viewModel::onGoalAmountChange,
                    isLoading = state.isLoading,
                    onComplete = { viewModel.completeOnboarding(onOnboardingComplete) },
                    onBack = viewModel::prevStep
                )
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(totalSteps) { index ->
            val isActive = index == currentStep
            val isPast = index < currentStep
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .width(if (isActive) 32.dp else 16.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isActive -> Primary
                            isPast -> Primary.copy(alpha = 0.5f)
                            else -> OnSurface.copy(alpha = 0.3f)
                        }
                    )
            )
        }
    }
}

@Composable
private fun NameStep(
    name: String,
    onNameChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "👋",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "What should we\ncall you?",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "We'll personalize your experience",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))
        FloTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = "Your name",
            modifier = Modifier.focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { if (name.isNotBlank()) onNext() })
        )
        Spacer(modifier = Modifier.height(32.dp))
        FloButton(
            text = "Continue →",
            onClick = onNext,
            enabled = name.isNotBlank()
        )
    }
}

@Composable
private fun IncomeStep(
    income: String,
    budget: String,
    onIncomeChange: (String) -> Unit,
    onBudgetChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("💰", style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Your monthly\nincome",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This helps calculate your savings rate",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))
        FloTextField(
            value = income,
            onValueChange = onIncomeChange,
            placeholder = "Monthly income (₹)",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        FloTextField(
            value = budget,
            onValueChange = onBudgetChange,
            placeholder = "Monthly budget (₹) — optional",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { if (income.isNotBlank()) onNext() })
        )
        Spacer(modifier = Modifier.height(32.dp))
        FloButton(
            text = "Continue →",
            onClick = onNext,
            enabled = income.isNotBlank()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBack) {
            Text("← Back", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun GoalStep(
    goalTitle: String,
    goalAmount: String,
    onGoalTitleChange: (String) -> Unit,
    onGoalAmountChange: (String) -> Unit,
    isLoading: Boolean,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🎯", style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Set your first\nsavings goal",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You can always change this later",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "GOAL NAME",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        FloTextField(
            value = goalTitle,
            onValueChange = onGoalTitleChange,
            placeholder = "e.g. Emergency Fund, Trip to Goa...",
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "TARGET AMOUNT",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        FloTextField(
            value = goalAmount,
            onValueChange = onGoalAmountChange,
            placeholder = "₹ 0",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onComplete() })
        )
        Spacer(modifier = Modifier.height(32.dp))
        FloButton(
            text = if (isLoading) "Setting up..." else "Let's go 🚀",
            onClick = onComplete,
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBack) {
            Text("← Back", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}


@Composable
fun FloTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            cursorColor = Primary
        )
    )
}

@Composable
fun FloButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = Color.Black,
            disabledContainerColor = Primary.copy(alpha = 0.3f),
            disabledContentColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}