package com.myfirsteverapp.newsaggregator.data.repository

import com.myfirsteverapp.newsaggregator.data.remote.NewsApiService
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.domain.model.Source
import com.myfirsteverapp.newsaggregator.domain.repository.NewsRepository
import com.myfirsteverapp.newsaggregator.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myfirsteverapp.newsaggregator.domain.model.Category
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
                        id = dto.url.hashCode().toString(),
                        title = dto.title,
                        description = dto.description ?: "",
                        content = dto.content ?: "",
                        url = dto.url,
                        urlToImage = dto.urlToImage ?: "",
                        author = dto.author,
                        publishedAt = dto.publishedAt ?: "",
                        source = Source(
                            id = dto.source?.id,
                            name = dto.source?.name ?: "Unknown"
                        ),
                        category = Category.ALL,  // Default or from param
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
                        id = dto.url.hashCode().toString(),
                        title = dto.title,
                        description = dto.description ?: "",
                        content = dto.content ?: "",
                        url = dto.url,
                        urlToImage = dto.urlToImage ?: "",
                        author = dto.author,
                        publishedAt = dto.publishedAt ?: "",
                        source = Source(
                            id = dto.source?.id,
                            name = dto.source?.name ?: "Unknown"
                        ),
                        category = Category.ALL,
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

    override suspend fun getBookmarkedArticles(): Flow<List<Article>> = flow {
        try {
            val userId = auth.currentUser?.uid ?: return@flow emit(emptyList())

            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("bookmarks")
                .get()
                .await()

            val articles = snapshot.documents.mapNotNull { doc ->
                try {
                    Article(
                        id = doc.id,
                        title = doc.getString("title") ?: return@mapNotNull null,
                        description = doc.getString("description") ?: "",
                        content = doc.getString("content") ?: "",
                        url = doc.getString("url") ?: return@mapNotNull null,
                        urlToImage = doc.getString("urlToImage") ?: "",
                        author = doc.getString("author"),
                        publishedAt = doc.getString("publishedAt") ?: "",
                        source = Source(
                            id = null,
                            name = doc.getString("source") ?: "Unknown"
                        ),
                        category = Category.ALL,
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
                "content" to article.content,
                "url" to article.url,
                "urlToImage" to article.urlToImage,
                "author" to article.author,
                "publishedAt" to article.publishedAt,
                "source" to article.source.name,
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