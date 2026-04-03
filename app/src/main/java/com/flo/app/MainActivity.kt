package com.flo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val isOnboarded by viewModel.isOnboarded.collectAsState()

            FloTheme(darkTheme = isDarkTheme) {

                if (isOnboarded == null) return@FloTheme

                val startDestination = if (isOnboarded == true)
                    NavRoutes.HOME else NavRoutes.ONBOARDING

                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavBar(navController) },
                    containerColor = MaterialTheme.colorScheme.background
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