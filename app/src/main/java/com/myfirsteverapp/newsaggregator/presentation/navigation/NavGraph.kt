package com.myfirsteverapp.newsaggregator.presentation.navigation

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.presentation.detail.ArticleDetailScreen
import com.myfirsteverapp.newsaggregator.presentation.main.MainScreen
import com.myfirsteverapp.newsaggregator.presentation.screens.auth.LoginScreen
import com.myfirsteverapp.newsaggregator.presentation.screens.auth.RegisterScreen
import com.myfirsteverapp.newsaggregator.presentation.search.SearchScreen
import com.myfirsteverapp.newsaggregator.presentation.splash.SplashScreen
import com.myfirsteverapp.newsaggregator.presentation.webview.ArticleWebViewScreen
import com.google.gson.Gson

private const val TAG = "NavGraph"

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
            val context = LocalContext.current
            val articleJson = backStackEntry.arguments?.getString("articleJson")
            val article = articleJson
                ?.let { runCatching { Uri.decode(it) } }
                ?.onFailure { throwable ->
                    Log.e(TAG, "Failed to decode article JSON argument", throwable)
                }
                ?.getOrNull()
                ?.let { decoded ->
                    runCatching { Gson().fromJson(decoded, Article::class.java) }
                        .onFailure { throwable ->
                            Log.e(TAG, "Failed to parse Article from JSON", throwable)
                        }
                        .getOrNull()
                }

            if (article == null) {
                LaunchedEffect(Unit) {
                    Toast.makeText(
                        context,
                        "Unable to open this article. Please try again.",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.popBackStack()
                }
            } else {
                ArticleDetailScreen(
                    article = article,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onReadFullArticle = { url ->
                        val route = Screen.ArticleWebView.createRoute(url)
                        navController.navigate(route)
                    }
                )
            }
        }

        // Article WebView Screen
        composable(
            route = Screen.ArticleWebView.route,
            arguments = listOf(
                navArgument("articleUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val context = LocalContext.current  // ← Read in composition
            val articleUrl = backStackEntry.arguments?.getString("articleUrl")
                ?.let { Uri.decode(it) } ?: ""

            if (articleUrl.isEmpty()) {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, "Invalid article URL", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            } else {
                ArticleWebViewScreen(
                    url = articleUrl,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}