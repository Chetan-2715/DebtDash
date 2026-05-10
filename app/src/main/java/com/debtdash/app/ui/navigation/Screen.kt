package com.debtdash.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Bottom navigation destinations for DebtDash.
 */
sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Nerve : Screen("nerve", "Nerve", Icons.Default.GridView)
    data object Split : Screen("split", "Split", Icons.AutoMirrored.Filled.CallSplit)
    data object Shame : Screen("shame", "Shame", Icons.Default.Warning)
    data object Match : Screen("match", "Match", Icons.AutoMirrored.Filled.CompareArrows)
    data object System : Screen("system", "System", Icons.Default.Settings)

    companion object {
        val items get() = listOf(Nerve, Split, Shame, Match, System)
    }
}
