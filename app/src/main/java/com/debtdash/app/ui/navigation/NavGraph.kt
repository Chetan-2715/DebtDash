package com.debtdash.app.ui.navigation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.debtdash.app.ui.screens.*
import com.debtdash.app.ui.theme.BackgroundPure

/**
 * Root composable — Gates on permissions, then shows Scaffold with bottom nav.
 * Consolidates UI into Nerve, Friends, Match, and System.
 */
@Composable
fun DebtDashApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hasSeenOnboarding = remember {
        context.getSharedPreferences("debtdash_prefs", Context.MODE_PRIVATE)
            .getBoolean("onboarding_complete", false)
    }

    val startDest = if (hasSeenOnboarding) Screen.Nerve.route else "onboarding"
    val showBottomBar = currentRoute != null && currentRoute != "onboarding" && !currentRoute.startsWith("split")

    Scaffold(
        containerColor = BackgroundPure,
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ── Onboarding Gate ──
            composable("onboarding") {
                OnboardingScreen(
                    onAllGranted = {
                        context.getSharedPreferences("debtdash_prefs", Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean("onboarding_complete", true)
                            .apply()
                        navController.navigate(Screen.Nerve.route) {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }

            // ── Main Tabs ──
            composable(Screen.Nerve.route) {
                DashboardScreen(
                    onTransactionClick = { transactionId, amount, reason ->
                        navController.navigate("split?txId=$transactionId&amount=$amount&reason=${reason ?: ""}")
                    },
                    onManualEntry = {
                        navController.navigate("split")
                    }
                )
            }

            composable(Screen.Friends.route) {
                FriendsScreen()
            }

            composable(Screen.Business.route) {
                BusinessScreen()
            }

            composable(Screen.Match.route) { MatchScreen() }
            composable(Screen.System.route) { SettingsScreen() }

            // ── Hidden Utility Screens ──
            composable(
                route = "split?txId={txId}&amount={amount}&reason={reason}",
                arguments = listOf(
                    navArgument("txId") { type = NavType.LongType; defaultValue = -1L },
                    navArgument("amount") { type = NavType.StringType; defaultValue = "" },
                    navArgument("reason") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val txId = backStackEntry.arguments?.getLong("txId") ?: -1L
                val prefillAmount = backStackEntry.arguments?.getString("amount") ?: ""
                val prefillReason = backStackEntry.arguments?.getString("reason") ?: ""
                SplitScreen(
                    prefillTransactionId = txId,
                    prefillAmount = prefillAmount,
                    prefillReason = prefillReason,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
