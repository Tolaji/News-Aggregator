package com.myfirsteverapp.newsaggregator.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.presentation.factcheck.FactCheckScreen
import com.myfirsteverapp.newsaggregator.presentation.screens.home.HomeScreen
import com.myfirsteverapp.newsaggregator.presentation.navigation.BottomNavItem
import com.myfirsteverapp.newsaggregator.presentation.screens.profile.ProfileScreen
import com.myfirsteverapp.newsaggregator.presentation.saved.SavedScreen

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

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Saved,
        BottomNavItem.FactCheck,
        BottomNavItem.Profile
    )

    Scaffold(
        topBar = {
            if (currentRoute == BottomNavItem.Home.route) {
                TopAppBar(
                    title = { Text("News Aggregator") },
                    actions = {
                        IconButton(onClick = onSearchClick) {
                            Icon(Icons.Default.Search, "Search")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(BottomNavItem.Home.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
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
                HomeScreen(onArticleClick = onArticleClick)
            }

            composable(BottomNavItem.Saved.route) {
                SavedScreen(onArticleClick = onArticleClick)
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