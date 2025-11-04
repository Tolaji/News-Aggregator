package com.myfirsteverapp.newsaggregator.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.myfirsteverapp.newsaggregator.presentation.screens.auth.AuthViewModel
import com.myfirsteverapp.newsaggregator.presentation.screens.auth.LoginScreen
import com.myfirsteverapp.newsaggregator.presentation.screens.auth.RegisterScreen
import com.myfirsteverapp.newsaggregator.presentation.screens.detail.ArticleDetailScreen
import com.myfirsteverapp.newsaggregator.presentation.screens.home.HomeScreen
import com.myfirsteverapp.newsaggregator.presentation.screens.saved.SavedScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Saved : Screen("saved")
    object Search : Screen("search")
    object ArticleDetail : Screen("article_detail/{articleId}") {
        fun createRoute(articleId: String) = "article_detail/$articleId"
    }
}

@Composable
fun NewsAggregatorNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.uiState.collectAsState()

    val startDestination = if (authState.isAuthenticated) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
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

        // Main screens
        composable(Screen.Home.route) {
            HomeScreen(
                onArticleClick = { article ->
                    navController.navigate(Screen.ArticleDetail.createRoute(article.id))
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }

        composable(Screen.Saved.route) {
            SavedScreen(
                onArticleClick = { article ->
                    navController.navigate(Screen.ArticleDetail.createRoute(article.id))
                }
            )
        }

        composable(
            route = Screen.ArticleDetail.route,
            arguments = listOf(
                navArgument("articleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString("articleId")
            // You would fetch article by ID here
            // For simplicity, passing from previous screen or cache
        }
    }
}