package com.myfirsteverapp.newsaggregator.data.repository

import com.myfirsteverapp.newsaggregator.data.remote.firebase.FirestoreManager
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedArticlesRepository @Inject constructor(
    private val firestoreManager: FirestoreManager
) {

    fun getSavedArticles(): Flow<Resource<List<Article>>> {
        return firestoreManager.getSavedArticles().map { result ->
            result.fold(
                onSuccess = { articles -> Resource.Success(articles) },
                onFailure = { error ->
                    Resource.Error(error.localizedMessage ?: "Failed to load saved articles")
                }
            )
        }
    }

    suspend fun saveArticle(article: Article): Result<Unit> {
        return firestoreManager.saveArticle(article)
    }

    suspend fun deleteArticle(articleId: String): Result<Unit> {
        return firestoreManager.deleteArticle(articleId)
    }

    suspend fun updateReadStatus(articleId: String, isRead: Boolean): Result<Unit> {
        return firestoreManager.updateArticleReadStatus(articleId, isRead)
    }
}