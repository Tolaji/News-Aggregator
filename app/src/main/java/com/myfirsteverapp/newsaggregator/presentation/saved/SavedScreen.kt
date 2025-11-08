package com.myfirsteverapp.newsaggregator.presentation.saved

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.presentation.components.ArticleCard
import com.myfirsteverapp.newsaggregator.presentation.components.EmptySavedArticles
import com.myfirsteverapp.newsaggregator.presentation.components.ErrorState
import com.myfirsteverapp.newsaggregator.presentation.components.LoadingState
import com.myfirsteverapp.newsaggregator.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    onArticleClick: (Article) -> Unit,
    viewModel: SavedViewModel = hiltViewModel()
) {
    val savedArticles by viewModel.savedArticles.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Saved Articles") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        when (savedArticles) {
            is Resource.Loading -> {
                LoadingState("Loading saved articles...")
            }

            is Resource.Success -> {
                val articles = (savedArticles as Resource.Success).data ?: emptyList()

                if (articles.isEmpty()) {
                    EmptySavedArticles()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "${articles.size} saved article${if (articles.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        items(articles, key = { it.url }) { article ->
                            ArticleCard(
                                article = article,
                                onClick = { onArticleClick(article) },
                                onBookmarkClick = { viewModel.toggleBookmark(article) }
                            )
                        }
                    }
                }
            }

            is Resource.Error -> {
                ErrorState(
                    message = (savedArticles as Resource.Error).message ?: "Failed to load saved articles",
                    onRetry = { viewModel.loadSavedArticles() }
                )
            }

            else -> {
                EmptySavedArticles()
            }
        }
    }
}