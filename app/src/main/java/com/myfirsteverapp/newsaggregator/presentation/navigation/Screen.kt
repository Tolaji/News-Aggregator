package com.myfirsteverapp.newsaggregator.presentation.navigation

import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.google.gson.Gson

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object Home : Screen("home")
    object Saved : Screen("saved")
    object ArticleDetail : Screen("article_detail/{articleJson}") {
        fun createRoute(article: Article): String {
            val json = Gson().toJson(article)
            return "article_detail/$json"
        }
    }
    object Search : Screen("search")
    object FactCheck : Screen("fact_check")
    object Profile : Screen("profile")
}