package com.flo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.flo.app.ui.navigation.BottomNavBar
import com.flo.app.ui.navigation.FloNavGraph
import com.flo.app.ui.navigation.NavRoutes
import com.flo.app.ui.screens.main.MainViewModel
import com.flo.app.ui.theme.FloTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.collectAsState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val isOnboarded by viewModel.isOnboarded.collectAsState()

            FloTheme(darkTheme = isDarkTheme) {
                // Show nothing until we know onboarding state
                if (isOnboarded == null) return@FloTheme

                val startDestination = if (isOnboarded == true)
                    NavRoutes.HOME else NavRoutes.ONBOARDING

                val navController = rememberNavController()

                Surface(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        bottomBar = { BottomNavBar(navController) }
                    ) { paddingValues ->
                        FloNavGraph(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }
}