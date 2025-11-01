// ui/screens/ArticleDetailScreen.kt
package com.yourname.newsaggregator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.myfirsteverapp.newsaggregator.model.FakeData
import com.myfirsteverapp.newsaggregator.ui.components.AppBottomNavigation

@Composable
fun ArticleDetailScreen(
    articleId: String,
    onNavigate: (String) -> Unit,
    onReadOriginal: (String) -> Unit
) {
    val article = FakeData.getArticleDetail(articleId)

    Scaffold(
        bottomBar = {
            AppBottomNavigation(selectedItem = "none", onItemSelected = onNavigate)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            item {
                AsyncImage(
                    model = article.imageUrl,
                    contentDescription = article.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.h5,
                        color = MaterialTheme.colors.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${article.source} • ${article.publishedAgo()}",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { /* Share */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                        IconButton(onClick = { /* Bookmark */ }) {
                            Icon(
                                imageVector = if (article.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Article body
                    Text(
                        text = article.body,
                        style = MaterialTheme.typography.body1,
                        lineHeight = androidx.compose.ui.unit.TextUnit(20.sp.value, androidx.compose.ui.unit.TextUnitType.Sp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Read original button
                    Button(
                        onClick = { onReadOriginal("https://example.com/article/$articleId") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Read Original Source")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}