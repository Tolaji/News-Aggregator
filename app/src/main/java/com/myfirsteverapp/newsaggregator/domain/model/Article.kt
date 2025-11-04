package com.myfirsteverapp.newsaggregator.domain.model

import java.util.Date

data class Article(
    val id: String,
    val title: String,
    val description: String,
    val content: String,
    val url: String,
    val imageUrl: String?,
    val author: String?,
    val source: Source,
    val publishedAt: Date,
    val category: Category,
    val isBookmarked: Boolean = false,
    val isFavorite: Boolean = false,
    val readStatus: ReadStatus = ReadStatus.UNREAD
) {
    // Kotlin function demonstrating expressions
    fun getTimeAgo(): String {
        val diff = Date().time - publishedAt.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        // When expression (Kotlin requirement)
        return when {
            days > 0 -> "${days}d ago"
            hours > 0 -> "${hours}h ago"
            minutes > 0 -> "${minutes}m ago"
            else -> "Just now"
        }
    }

    // Demonstrates immutable variable usage
    val isRecent: Boolean
        get() = (Date().time - publishedAt.time) < 24 * 60 * 60 * 1000
}

data class Source(
    val id: String?,
    val name: String
)

// Enum demonstrating Kotlin classes
enum class Category(val displayName: String) {
    ALL("All"),
    POLITICS("Politics"),
    TECH("Technology"),
    SPORTS("Sports"),
    BUSINESS("Business"),
    SCIENCE("Science"),
    HEALTH("Health"),
    ENTERTAINMENT("Entertainment");

    companion object {
        // Demonstrates collections
        fun getApiCategory(category: Category): String? {
            return when (category) {
                ALL -> null
                else -> category.name.lowercase()
            }
        }
    }
}

enum class ReadStatus {
    UNREAD,
    READ,
    ARCHIVED
}