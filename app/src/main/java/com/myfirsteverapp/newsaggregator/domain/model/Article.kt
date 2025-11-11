package com.myfirsteverapp.newsaggregator.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    val id: String = "",  // Default for mapper
    val title: String,
    val description: String?,
    val content: String?,
    val url: String,
    val urlToImage: String?,
    val author: String?,
    val publishedAt: String,
    val source: Source,
    val category: Category,
    val isSaved: Boolean = false,
    val isRead: Boolean = false,
    val isBookmarked: Boolean = false
) : Parcelable

@Parcelize
data class Source(
    val id: String?,
    val name: String
) : Parcelable