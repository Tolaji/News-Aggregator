package com.myfirsteverapp.newsaggregator.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myfirsteverapp.newsaggregator.presentation.screens.auth.AuthViewModel
import com.myfirsteverapp.newsaggregator.presentation.screens.auth.LoginScreen
import com.myfirsteverapp.newsaggregator.presentation.screens.auth.ProfileRoute
import com.myfirsteverapp.newsaggregator.presentation.screens.auth.RegisterScreen
import com.myfirsteverapp.newsaggregator.presentation.screens.home.HomeScreen
import com.myfirsteverapp.newsaggregator.presentation.screens.saved.SavedScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Saved : Screen("saved")
    object Profile : Screen("profile")
    object Search : Screen("search")
}

@Composable
fun NewsAggregatorNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val startDestination = if (authState.isAuthenticated) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    // Routes that should show bottom navigation
    val routesWithBottomNav = setOf(
        Screen.Home.route,
        Screen.Saved.route,
        Screen.Profile.route
    )

    val showBottomNav = currentRoute in routesWithBottomNav

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // Pop up to start destination to avoid building up stack
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Auth screens
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                )
            }

            // Main screens with bottom nav
            composable(Screen.Home.route) {
                HomeScreen(
                    onArticleClick = { article ->
                        // For now, just log - we'll implement detail screen next
                        println("Clicked article: ${article.title}")
                    },
                    onSearchClick = {
                        // Navigate to search if implemented
                    }
                )
            }

            composable(Screen.Saved.route) {
                SavedScreen(
                    onArticleClick = { article ->
                        println("Clicked saved article: ${article.title}")
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileRoute(
                    onSignedOut = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}