package com.myfirsteverapp.newsaggregator.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )
    object Saved : BottomNavItem(
        route = "saved",
        title = "Saved",
        icon = Icons.Default.Bookmark
    )
    object FactCheck : BottomNavItem(
        route = "fact_check",
        title = "Fact Check",
        icon = Icons.Default.Verified
    )
    object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person
    )
}