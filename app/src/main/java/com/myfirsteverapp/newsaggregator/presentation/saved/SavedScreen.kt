package com.myfirsteverapp.newsaggregator.presentation.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.presentation.components.ArticleCard
import com.myfirsteverapp.newsaggregator.presentation.components.EmptySavedArticles
import com.myfirsteverapp.newsaggregator.presentation.components.ErrorState
import com.myfirsteverapp.newsaggregator.presentation.components.LoadingState
import com.myfirsteverapp.newsaggregator.presentation.screens.saved.SavedViewModel
import com.myfirsteverapp.newsaggregator.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    onArticleClick: (Article) -> Result<Unit>,
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
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { value ->
                                    if (value == SwipeToDismissBoxValue.EndToStart ||
                                        value == SwipeToDismissBoxValue.StartToEnd
                                    ) {
                                        viewModel.toggleBookmark(article)
                                        true
                                    } else {
                                        false
                                    }
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = true,
                                enableDismissFromEndToStart = true,
                                backgroundContent = {
                                    SavedSwipeBackground(
                                        dismissState = dismissState
                                    )
                                }
                            ) {
                                ArticleCard(
                                    article = article,
                                    onClick = { onArticleClick(article) },
                                    onBookmarkClick = { viewModel.toggleBookmark(article) }
                                )
                            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedSwipeBackground(
    dismissState: SwipeToDismissBoxState
) {
    val direction = dismissState.dismissDirection
    val isDismissed = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart ||
            dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd

    val alignment = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        else -> Alignment.CenterEnd
    }

    val color = if (isDismissed) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Surface(
        color = color,
        modifier = Modifier.fillMaxSize()
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = alignment
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete saved article",
                tint = if (isDismissed) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
