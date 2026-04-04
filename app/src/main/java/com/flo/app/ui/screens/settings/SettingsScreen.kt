package com.flo.app.ui.screens.settings

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flo.app.ui.components.FloCard
import com.flo.app.ui.theme.Primary

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showNameDialog by remember { mutableStateOf(false) }
    var showIncomeDialog by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Profile section
        SectionLabel("Profile")
        FloCard(modifier = Modifier.fillMaxWidth()) {
            SettingsRow(
                icon = Icons.Rounded.Person,
                label = "Name",
                value = state.userName.ifBlank { "Not set" },
                onClick = { showNameDialog = true }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            SettingsRow(
                icon = Icons.Rounded.Wallet,
                label = "Monthly Income",
                value = if (state.monthlyIncome.isNotBlank()) "₹${state.monthlyIncome}" else "Not set",
                onClick = { showIncomeDialog = true }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            SettingsRow(
                icon = Icons.Rounded.PieChart,
                label = "Monthly Budget",
                value = if (state.monthlyBudget.isNotBlank()) "₹${state.monthlyBudget}" else "Not set",
                onClick = { showBudgetDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Appearance section
        SectionLabel("Appearance")
        FloCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Rounded.DarkMode,
                        contentDescription = null,
                        tint = Primary
                    )
                    Text(
                        "Dark Mode",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Switch(
                    checked = state.isDarkTheme,
                    onCheckedChange = viewModel::toggleTheme,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.background,
                        checkedTrackColor = Primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // About section
        SectionLabel("About")
        FloCard(modifier = Modifier.fillMaxWidth()) {
            SettingsInfoRow(label = "Version", value = "1.0.0")
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            SettingsInfoRow(label = "Built with", value = "Kotlin + Jetpack Compose")
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            SettingsInfoRow(label = "Architecture", value = "MVVM + Clean Architecture")
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    // Dialogs
    if (showNameDialog) {
        EditDialog(
            title = "Edit Name",
            currentValue = state.userName,
            placeholder = "Your name",
            onConfirm = {
                viewModel.saveName(it)
                showNameDialog = false
            },
            onDismiss = { showNameDialog = false }
        )
    }

    if (showIncomeDialog) {
        EditDialog(
            title = "Monthly Income",
            currentValue = state.monthlyIncome,
            placeholder = "Amount in ₹",
            isNumeric = true,
            onConfirm = {
                viewModel.saveIncome(it)
                showIncomeDialog = false
            },
            onDismiss = { showIncomeDialog = false }
        )
    }

    if (showBudgetDialog) {
        EditDialog(
            title = "Monthly Budget",
            currentValue = state.monthlyBudget,
            placeholder = "Amount in ₹",
            isNumeric = true,
            onConfirm = {
                viewModel.saveBudget(it)
                showBudgetDialog = false
            },
            onDismiss = { showBudgetDialog = false }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = Primary,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(icon, contentDescription = null, tint = Primary)
                Text(
                    label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SettingsInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EditDialog(
    title: String,
    currentValue: String,
    placeholder: String,
    isNumeric: Boolean = false,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var value by remember { mutableStateOf(currentValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                placeholder = {
                    Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = if (isNumeric)
                    androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                else androidx.compose.foundation.text.KeyboardOptions.Default,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(value) }) {
                Text("Save", color = Primary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}