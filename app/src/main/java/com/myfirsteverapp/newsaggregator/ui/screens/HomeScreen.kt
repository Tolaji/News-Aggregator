// ui/screens/HomeScreen.kt
package com.yourname.newsaggregator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.yourname.newsaggregator.model.Article
import com.yourname.newsaggregator.model.FakeData
import com.yourname.newsaggregator.ui.components.AppBottomNavigation
import com.yourname.newsaggregator.ui.components.ArticleCard
import com.yourname.newsaggregator.ui.components.CategoryRow

@Composable
fun HomeScreen(
    onArticleClick: (Article) -> Unit,
    onNavigate: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = FakeData.categories
    val articles = FakeData.homeArticles.filter {
        selectedCategory == "All" || it.category == selectedCategory
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News Aggregator") },
                actions = {
                    IconButton(onClick = { /* open search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White
            )
        },
        bottomBar = {
            AppBottomNavigation(selectedItem = "Home", onItemSelected = onNavigate)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            CategoryRow(
                categories = categories,
                selectedCategory = selectedCategory,
                onSelect = { selectedCategory = it }
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(articles.size) { index ->
                    val article = articles[index]
                    ArticleCard(
                        article = article,
                        onClick = { onArticleClick(article) },
                        onToggleBookmark = {
                            // In real app, this would update in ViewModel
                            println("Toggled bookmark for ${article.title}")
                        }
                    )
                }
            }
        }
    }
}