package com.nexus.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nexus.app.ui.screen.agenda.AgendaRoute
import com.nexus.app.ui.screen.biometrics.BiometricsRoute
import com.nexus.app.ui.screen.input.InputRoute
import com.nexus.app.ui.screen.settings.SettingsRoute

import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.nexus.app.ui.screen.onboarding.OnboardingScreen

@Composable
fun NexusRoot(
    viewModel: NexusViewModel = hiltViewModel()
) {
    val showOnboarding by viewModel.showOnboarding.collectAsState()

    if (showOnboarding) {
        OnboardingScreen(onComplete = { viewModel.completeOnboarding() })
    } else {
        MainContent()
    }
}

@Composable
private fun MainContent() {
    val navController = rememberNavController()
    val items = listOf(
        TopLevelDestination.Input,
        TopLevelDestination.Agenda,
        TopLevelDestination.Biometrics,
        TopLevelDestination.Settings,
    )
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.Input.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(TopLevelDestination.Input.route) { InputRoute() }
            composable(TopLevelDestination.Agenda.route) { AgendaRoute() }
            composable(TopLevelDestination.Biometrics.route) { BiometricsRoute() }
            composable(TopLevelDestination.Settings.route) { SettingsRoute() }
        }
    }
}

private sealed class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    data object Input : TopLevelDestination("input", "Capture", Icons.Default.EditNote)
    data object Agenda : TopLevelDestination("agenda", "Agenda", Icons.AutoMirrored.Filled.EventNote)
    data object Biometrics : TopLevelDestination("biometrics", "Biometrics", Icons.Default.FitnessCenter)
    data object Settings : TopLevelDestination("settings", "Settings", Icons.Default.Settings)
}
