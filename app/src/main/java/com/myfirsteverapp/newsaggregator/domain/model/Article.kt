package com.myfirsteverapp.newsaggregator.domain.model

data class Article(
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val source: String,
    val author: String? = null,
    val content: String? = null,
    val isBookmarked: Boolean = false
)