package com.myfirsteverapp.newsaggregator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.myfirsteverapp.newsaggregator.ui.theme.NewsAggregatorTheme
import androidx.compose.runtime.*
import com.myfirsteverapp.newsaggregator.model.Article
import com.myfirsteverapp.newsaggregator.ui.screens.ArticleDetailScreen
import com.myfirsteverapp.newsaggregator.ui.screens.HomeScreen
import com.myfirsteverapp.newsaggregator.ui.screens.SavedScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsAggregatorTheme {
                NewsAggregatorApp()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )

                }

            }

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NewsAggregatorTheme {
        Greeting("Android")
    }
}

@Composable
fun NewsAggregatorApp() {
    var currentScreen by remember { mutableStateOf("Home") }
    var selectedArticleId by remember { mutableStateOf<String?>(null) }

    when {
        selectedArticleId != null -> {
            ArticleDetailScreen(
                articleId = selectedArticleId!!,
                onNavigate = { screen ->
                    if (screen != "none") {
                        currentScreen = screen
                        selectedArticleId = null
                    }
                },
                onReadOriginal = { url ->
                    // In real app, open Chrome Custom Tab
                    println("Opening: $url")
                }
            )
        }
        else -> {
            when (currentScreen) {
                "Home" -> HomeScreen(
                    onArticleClick = { article: Article ->
                        selectedArticleId = article.id
                    },
                    onNavigate = { screen -> currentScreen = screen }
                )
                "Saved" -> SavedScreen(
                    onArticleClick = { article: Article ->
                        selectedArticleId = article.id
                    },
                    onNavigate = { screen -> currentScreen = screen }
                )
                else -> HomeScreen(
                    onArticleClick = { article: Article ->
                        selectedArticleId = article.id
                    },
                    onNavigate = { screen -> currentScreen = screen }
                )
            }
        }
    }
}