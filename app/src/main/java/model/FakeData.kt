// model/FakeData.kt
package com.yourname.newsaggregator.model

object FakeData {
    val categories = listOf("All", "Politics", "Tech", "Sports", "Science", "World")

    private val sampleArticle = Article(
        id = "1",
        title = "The Future of AI: How Machine Learning is Transforming Industries Globally",
        subtitle = "A detailed look at the impact of deep learning",
        imageUrl = "https://picsum.photos/400/225", // Using placeholder service
        source = "TechCrunch",
        publishedDate = "2025-10-31T10:00:00Z",
        body = "Artificial intelligence (AI) continues its rapid expansion...",
        isBookmarked = false,
        category = "Tech"
    )

    val homeArticles = listOf(
        sampleArticle,
        sampleArticle.copy(id = "2", title = "Global Leaders Meet to Address Climate Change", source = "Reuters", isBookmarked = true, category = "Politics"),
        sampleArticle.copy(id = "3", title = "New Record Set in Marathon by Unknown Runner", source = "ESPN", category = "Sports"),
        sampleArticle.copy(id = "4", title = "Revolutionary Battery Tech Promises Week-Long Phone Life", source = "Wired", category = "Tech"),
    )

    val savedArticles = homeArticles.filter { it.isBookmarked }

    fun getArticleDetail(id: String) = homeArticles.firstOrNull { it.id == id } ?: sampleArticle
}