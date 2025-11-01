// model/Article.kt
package com.yourname.newsaggregator.model

data class Article(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val source: String,
    val publishedDate: String,
    val body: String,
    val isBookmarked: Boolean = false,
    val category: String
) {
    fun publishedAgo(): String {
        // Simplified - in real app, calculate from publishedDate
        return "2h ago"
    }
}