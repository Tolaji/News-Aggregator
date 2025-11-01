// ui/screens/SavedScreen.kt
package com.yourname.newsaggregator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yourname.newsaggregator.model.Article
import com.yourname.newsaggregator.model.FakeData
import com.yourname.newsaggregator.ui.components.AppBottomNavigation
import com.yourname.newsaggregator.ui.components.ArticleCard

@Composable
fun SavedScreen(
    onArticleClick: (Article) -> Unit,
    onNavigate: (String) -> Unit
) {
    val savedArticles = FakeData.savedArticles

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Articles") },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White
            )
        },
        bottomBar = {
            AppBottomNavigation(selectedItem = "Saved", onItemSelected = onNavigate)
        }
    ) { padding ->
        if (savedArticles.isEmpty()) {
            EmptyState(
                message = "You haven't saved any articles yet.",
                ctaText = "Explore Topics",
                onCtaClick = { onNavigate("Explore") }
            )
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(savedArticles.size) { index ->
                    val article = savedArticles[index]
                    ArticleCard(
                        article = article,
                        onClick = { onArticleClick(article) },
                        onToggleBookmark = { /* Handle bookmark toggle */ }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(message: String, ctaText: String, onCtaClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Empty state",
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onCtaClick) {
            Text(ctaText)
        }
    }
}