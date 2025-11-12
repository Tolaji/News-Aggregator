package com.myfirsteverapp.newsaggregator.presentation.navigation

import android.net.Uri
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
        private const val ARG_ROUTE_PREFIX = "article_detail/"

        fun createRoute(article: Article): String {
            val json = Gson().toJson(article)
            val encodedJson = Uri.encode(json)
            return "$ARG_ROUTE_PREFIX$encodedJson"
        }
    }
    object Search : Screen("search")
    object FactCheck : Screen("fact_check")
    object Profile : Screen("profile")
    object ArticleWebView : Screen("article_webview/{articleUrl}") {
        fun createRoute(url: String): String {
            val encodedUrl = Uri.encode(url)
            return "article_webview/$encodedUrl"
        }
    }
}