package com.myfirsteverapp.newsaggregator.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.presentation.screens.auth.LoginScreen
import com.myfirsteverapp.newsaggregator.presentation.screens.auth.RegisterScreen
import com.myfirsteverapp.newsaggregator.presentation.detail.ArticleDetailScreen
import com.myfirsteverapp.newsaggregator.presentation.main.MainScreen
import com.myfirsteverapp.newsaggregator.presentation.search.SearchScreen
import com.myfirsteverapp.newsaggregator.presentation.splash.SplashScreen
import com.google.gson.Gson

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Register Screen
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Main Screen (with bottom navigation)
        composable(Screen.Main.route) {
            MainScreen(
                onArticleClick = { article ->
                    val route = Screen.ArticleDetail.createRoute(article)
                    navController.navigate(route)
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Search Screen
        composable(Screen.Search.route) {
            SearchScreen(
                onArticleClick = { article ->
                    val route = Screen.ArticleDetail.createRoute(article)
                    navController.navigate(route)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Article Detail Screen
        composable(
            route = Screen.ArticleDetail.route,
            arguments = listOf(
                navArgument("articleJson") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val articleJson = backStackEntry.arguments?.getString("articleJson")
            val article = Gson().fromJson(articleJson, Article::class.java)

            ArticleDetailScreen(
                article = article,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}