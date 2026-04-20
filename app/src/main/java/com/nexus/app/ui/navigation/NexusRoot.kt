package com.nexus.app.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nexus.app.ui.screen.agenda.AgendaRoute
import com.nexus.app.ui.screen.biometrics.BiometricsRoute
import com.nexus.app.ui.screen.input.InputRoute
import com.nexus.app.ui.screen.onboarding.OnboardingScreen
import com.nexus.app.ui.screen.settings.SettingsRoute
import com.nexus.app.ui.screen.splash.SplashScreen

import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun NexusRoot(
    viewModel: NexusViewModel = hiltViewModel(),
) {
    val showSplash by viewModel.showSplash.collectAsState()
    val showOnboarding by viewModel.showOnboarding.collectAsState()

    AnimatedContent(
        targetState = when {
            showSplash -> AppState.SPLASH
            showOnboarding -> AppState.ONBOARDING
            else -> AppState.MAIN
        },
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "app_state",
    ) { state ->
        when (state) {
            AppState.SPLASH -> SplashScreen(onSplashFinished = viewModel::splashFinished)
            AppState.ONBOARDING -> OnboardingScreen(
                onComplete = { apiKey, userName ->
                    viewModel.completeOnboarding(apiKey, userName)
                },
            )
            AppState.MAIN -> MainContent()
        }
    }
}

private enum class AppState { SPLASH, ONBOARDING, MAIN }

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
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 0.dp,
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                            )
                        },
                        label = {
                            Text(
                                item.label,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.5.sp,
                                ),
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
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
