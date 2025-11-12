package com.myfirsteverapp.newsaggregator.presentation.main

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.presentation.factcheck.FactCheckScreen
import com.myfirsteverapp.newsaggregator.presentation.navigation.BottomNavItem
import com.myfirsteverapp.newsaggregator.presentation.profile.ProfileScreen
import com.myfirsteverapp.newsaggregator.presentation.saved.SavedScreen
import com.myfirsteverapp.newsaggregator.presentation.screens.home.HomeScreen

private const val TAG = "MainScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onArticleClick: (Article) -> Unit,
    onSearchClick: () -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = remember {
        listOf(
            BottomNavItem.Home,
            BottomNavItem.Saved,
            BottomNavItem.FactCheck,
            BottomNavItem.Profile
        )
    }

    val navigateSafely = remember(navController) {
        { route: String ->
            runCatching {
                navController.navigate(route) {
                    popUpTo(BottomNavItem.Home.route) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }.onFailure { throwable ->
                Log.e(TAG, "Failed to navigate to $route", throwable)
            }
        }
    }

    val handleArticleClick = remember(onArticleClick) {
        { article: Article ->
            runCatching { onArticleClick(article) }
                .onFailure { throwable ->
                    Log.e(TAG, "Failed to open article ${article.title}", throwable)
                }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navigateSafely(item.route)
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onArticleClick = handleArticleClick,
                    onSearchClick = onSearchClick
                )
            }

            composable(BottomNavItem.Saved.route) {
                SavedScreen(onArticleClick = handleArticleClick)
            }

            composable(BottomNavItem.FactCheck.route) {
                FactCheckScreen()
            }

            composable(BottomNavItem.Profile.route) {
                ProfileScreen(onLogout = onLogout)
            }
        }
    }
}