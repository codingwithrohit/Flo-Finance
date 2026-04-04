package com.flo.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
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
                onAddTransaction = { navController.navigate(NavRoutes.ADD_TRANSACTION) },
                onSeeAllTransactions = { navController.navigate(NavRoutes.TRANSACTIONS) },
                onSettingsClick = { navController.navigate(NavRoutes.SETTINGS) }
            )
        }

        composable(NavRoutes.TRANSACTIONS) {
            TransactionsScreen(
                onAddTransaction = {
                    navController.navigate(NavRoutes.ADD_TRANSACTION)
                },
                onEditTransaction = { id ->
                    navController.navigate(NavRoutes.editTransaction(id))
                }
            )
        }

        composable(NavRoutes.ADD_TRANSACTION) {
            Box { }
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
            SettingsScreen()
        }
    }
}