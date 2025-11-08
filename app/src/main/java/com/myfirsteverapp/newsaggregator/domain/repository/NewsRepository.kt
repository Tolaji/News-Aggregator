package com.myfirsteverapp.newsaggregator.domain.repository

import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.util.Resource
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getTopHeadlines(category: String? = null): Flow<Resource<List<Article>>>
    suspend fun searchNews(query: String): Flow<Resource<List<Article>>>
    suspend fun getBookmarkedArticles(): Flow<List<Article>>
    suspend fun addBookmark(article: Article)
    suspend fun removeBookmark(article: Article)
    suspend fun isArticleBookmarked(articleUrl: String): Boolean
}