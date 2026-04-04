package com.flo.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flo.app.ui.screens.goals.GoalsScreen
import com.flo.app.ui.screens.home.HomeScreen
import com.flo.app.ui.screens.insights.InsightsScreen
import com.flo.app.ui.screens.onboarding.OnboardingScreen
import com.flo.app.ui.screens.settings.SettingsScreen
import com.flo.app.ui.screens.transactions.TransactionsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloNavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NavRoutes.ONBOARDING) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.HOME) {
            HomeScreen(
                onAddTransaction = {
                    navController.navigate(NavRoutes.transactionsWithSheet()) {
                        popUpTo(NavRoutes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                onSeeAllTransactions = { navController.navigate(NavRoutes.TRANSACTIONS) },
                onSettingsClick = { navController.navigate(NavRoutes.SETTINGS) }
            )
        }

        composable(NavRoutes.TRANSACTIONS) {
            TransactionsScreen(
                autoOpenSheet = false,
                onAddTransaction = {
                    navController.navigate(NavRoutes.transactionsWithSheet())
                },
                onEditTransaction = { id ->
                    navController.navigate(NavRoutes.editTransaction(id))
                }
            )
        }

        composable(
            route = NavRoutes.TRANSACTIONS_WITH_SHEET,
            arguments = listOf(navArgument("addNew") {
                type = NavType.BoolType
                defaultValue = false
            })
        ) { backStackEntry ->
            val addNew = backStackEntry.arguments?.getBoolean("addNew") ?: false
            TransactionsScreen(
                autoOpenSheet = addNew,
                onAddTransaction = {
                    navController.navigate(NavRoutes.transactionsWithSheet())
                },
                onEditTransaction = { id ->
                    navController.navigate(NavRoutes.editTransaction(id))
                }
            )
        }


        composable(NavRoutes.ADD_TRANSACTION) {
            LaunchedEffect(Unit) {
                navController.navigate(NavRoutes.transactionsWithSheet()) {
                    popUpTo(NavRoutes.ADD_TRANSACTION) { inclusive = true }
                }
            }
        }

        composable(
            route = NavRoutes.EDIT_TRANSACTION,
            arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: 0L
            // Placeholder
            Box { }
        }

        composable(NavRoutes.GOALS) {
            GoalsScreen()
        }

        composable(NavRoutes.INSIGHTS) {
            InsightsScreen()
        }

        composable(NavRoutes.SETTINGS) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "Settings",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.Rounded.ArrowBackIosNew,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { padding ->
                SettingsScreen(
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}