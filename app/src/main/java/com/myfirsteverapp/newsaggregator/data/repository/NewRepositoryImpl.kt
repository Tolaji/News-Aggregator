package com.myfirsteverapp.newsaggregator.data.repository

import com.myfirsteverapp.newsaggregator.data.remote.NewsApiService
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.domain.repository.NewsRepository
import com.myfirsteverapp.newsaggregator.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : NewsRepository {

    override suspend fun getTopHeadlines(category: String?): Flow<Resource<List<Article>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getTopHeadlines(category = category)

            if (response.status == "ok") {
                val articles = response.articles.mapNotNull { dto ->
                    // Skip articles with missing critical fields
                    if (dto.title == null || dto.url == null) return@mapNotNull null

                    Article(
                        title = dto.title,
                        description = dto.description ?: "",
                        url = dto.url,
                        urlToImage = dto.urlToImage ?: "",
                        publishedAt = dto.publishedAt ?: "",
                        source = dto.source?.name ?: "Unknown",
                        author = dto.author,
                        content = dto.content,
                        isBookmarked = isArticleBookmarked(dto.url)
                    )
                }
                emit(Resource.Success(articles))
            } else {
                emit(Resource.Error("Failed to fetch news"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override suspend fun searchNews(query: String): Flow<Resource<List<Article>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.searchNews(query = query)

            if (response.status == "ok") {
                val articles = response.articles.mapNotNull { dto ->
                    // Skip articles with missing critical fields
                    if (dto.title == null || dto.url == null) return@mapNotNull null

                    Article(
                        title = dto.title,
                        description = dto.description ?: "",
                        url = dto.url,
                        urlToImage = dto.urlToImage ?: "",
                        publishedAt = dto.publishedAt ?: "",
                        source = dto.source?.name ?: "Unknown",
                        author = dto.author,
                        content = dto.content,
                        isBookmarked = isArticleBookmarked(dto.url)
                    )
                }
                emit(Resource.Success(articles))
            } else {
                emit(Resource.Error("No results found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Search failed"))
        }
    }

    override suspend fun getBookmarkedArticles(): Flow<List<Article>> = flow {
        try {
            val userId = auth.currentUser?.uid ?: return@flow

            val snapshot = firestore
                .collection("users")
                .document(userId)
                .collection("bookmarks")
                .get()
                .await()

            val articles = snapshot.documents.mapNotNull { doc ->
                try {
                    Article(
                        title = doc.getString("title") ?: return@mapNotNull null,
                        description = doc.getString("description") ?: "",
                        url = doc.getString("url") ?: return@mapNotNull null,
                        urlToImage = doc.getString("urlToImage") ?: "",
                        publishedAt = doc.getString("publishedAt") ?: "",
                        source = doc.getString("source") ?: "Unknown",
                        author = doc.getString("author"),
                        content = doc.getString("content"),
                        isBookmarked = true
                    )
                } catch (e: Exception) {
                    null
                }
            }

            emit(articles)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun addBookmark(article: Article) {
        try {
            val userId = auth.currentUser?.uid ?: return

            val articleData = hashMapOf(
                "title" to article.title,
                "description" to article.description,
                "url" to article.url,
                "urlToImage" to article.urlToImage,
                "publishedAt" to article.publishedAt,
                "source" to article.source,
                "author" to article.author,
                "content" to article.content,
                "isBookmarked" to true
            )

            firestore
                .collection("users")
                .document(userId)
                .collection("bookmarks")
                .document(article.url.hashCode().toString())
                .set(articleData)
                .await()
        } catch (e: Exception) {
            // Handle error silently or log
        }
    }

    override suspend fun removeBookmark(article: Article) {
        try {
            val userId = auth.currentUser?.uid ?: return

            firestore
                .collection("users")
                .document(userId)
                .collection("bookmarks")
                .document(article.url.hashCode().toString())
                .delete()
                .await()
        } catch (e: Exception) {
            // Handle error silently or log
        }
    }

    override suspend fun isArticleBookmarked(articleUrl: String): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false

            val doc = firestore
                .collection("users")
                .document(userId)
                .collection("bookmarks")
                .document(articleUrl.hashCode().toString())
                .get()
                .await()

            doc.exists()
        } catch (e: Exception) {
            false
        }
    }
}