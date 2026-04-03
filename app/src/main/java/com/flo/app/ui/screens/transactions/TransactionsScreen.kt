package com.flo.app.ui.screens.transactions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun TransactionsScreen(
    onAddTransaction: () -> Unit = {},
    onEditTransaction: (Long) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Transactions", style = MaterialTheme.typography.headlineLarge)
    }
}