package com.myfirsteverapp.newsaggregator.presentation.navigation

import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.google.gson.Gson

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object ArticleDetail : Screen("article_detail/{articleJson}") {
        fun createRoute(article: Article): String {
            val json = Gson().toJson(article)
            return "article_detail/$json"
        }
    }
    object Search : Screen("search")
    object FactCheck : Screen("fact_check")
}

// Bottom navigation items
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        icon = androidx.compose.material.icons.Icons.Default.Home
    )
    object Saved : BottomNavItem(
        route = "saved",
        title = "Saved",
        icon = androidx.compose.material.icons.Icons.Default.Bookmark
    )
    object FactCheck : BottomNavItem(
        route = "fact_check",
        title = "Fact Check",
        icon = androidx.compose.material.icons.Icons.Default.Verified
    )
    object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        icon = androidx.compose.material.icons.Icons.Default.Person
    )
}