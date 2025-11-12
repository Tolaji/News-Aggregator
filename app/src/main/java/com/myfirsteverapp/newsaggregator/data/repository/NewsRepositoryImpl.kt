package com.myfirsteverapp.newsaggregator.data.repository

import android.util.Log
import com.myfirsteverapp.newsaggregator.data.remote.api.NewsApiService
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.domain.model.Source
import com.myfirsteverapp.newsaggregator.domain.repository.NewsRepository
import com.myfirsteverapp.newsaggregator.util.DateUtils
import com.myfirsteverapp.newsaggregator.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myfirsteverapp.newsaggregator.domain.model.Category
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
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

    companion object {
        private const val TAG = "NewsRepositoryImpl"
    }

    override suspend fun getTopHeadlines(category: String?): Flow<Resource<List<Article>>> = flow {
        try {
            Log.d(TAG, "📡 Starting API call - Category: $category")
            emit(Resource.Loading())

            val response = apiService.getTopHeadlines(category = category)
            
            Log.d(TAG, "✅ API Response received - Status: ${response.status}, Total Results: ${response.totalResults}")
            Log.d(TAG, "📰 Articles in response: ${response.articles.size}")

            if (response.status == "ok") {
                // Optimize: Fetch all bookmarks at once instead of checking individually
                val bookmarkedUrls = getBookmarkedUrlsSet()
                Log.d(TAG, "📚 Found ${bookmarkedUrls.size} bookmarked articles")
                
                val freshnessCutoff = DateUtils.hoursAgoInMillis(36)

                val articles = response.articles
                    .mapNotNull { dto ->
                        // Skip articles with missing critical fields
                        if (dto.title == null || dto.url == null) {
                            Log.w(TAG, "⚠️ Skipping article - missing title or URL")
                            return@mapNotNull null
                        }

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
                            isBookmarked = bookmarkedUrls.contains(dto.url.hashCode().toString())
                        )
                    }
                    .distinctBy { it.url }
                    .filter { article ->
                        val publishedAtMillis = DateUtils.parseIso8601ToMillis(article.publishedAt)
                        publishedAtMillis == 0L || publishedAtMillis >= freshnessCutoff
                    }
                    .sortedByDescending { article -> DateUtils.parseIso8601ToMillis(article.publishedAt) }
                
                Log.d(TAG, "✨ Successfully parsed ${articles.size} articles")
                if (articles.isNotEmpty()) {
                    Log.d(TAG, "📄 First article: ${articles.first().title}")
                }
                
                emit(Resource.Success(articles))
            } else {
                val errorMsg = "API returned status: ${response.status}"
                Log.e(TAG, "❌ $errorMsg")
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: e.message ?: "An unexpected error occurred"
            Log.e(TAG, "💥 Exception occurred: $errorMsg", e)
            emit(Resource.Error(errorMsg))
        }
    }

    override suspend fun searchNews(query: String): Flow<Resource<List<Article>>> = flow {
        try {
            Log.d(TAG, "🔍 Starting search API call - Query: $query")
            emit(Resource.Loading())

            val response = apiService.searchNews(query = query)
            
            Log.d(TAG, "✅ Search Response received - Status: ${response.status}, Total Results: ${response.totalResults}")
            Log.d(TAG, "📰 Articles in response: ${response.articles.size}")

            if (response.status == "ok") {
                // Optimize: Fetch all bookmarks at once instead of checking individually
                val bookmarkedUrls = getBookmarkedUrlsSet()
                Log.d(TAG, "📚 Found ${bookmarkedUrls.size} bookmarked articles")
                
                val freshnessCutoff = DateUtils.hoursAgoInMillis(36)

                val articles = response.articles
                    .mapNotNull { dto ->
                        // Skip articles with missing critical fields
                        if (dto.title == null || dto.url == null) {
                            Log.w(TAG, "⚠️ Skipping article - missing title or URL")
                            return@mapNotNull null
                        }

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
                            isBookmarked = bookmarkedUrls.contains(dto.url.hashCode().toString())
                        )
                    }
                    .distinctBy { it.url }
                    .filter { article ->
                        val publishedAtMillis = DateUtils.parseIso8601ToMillis(article.publishedAt)
                        publishedAtMillis == 0L || publishedAtMillis >= freshnessCutoff
                    }
                    .sortedByDescending { article -> DateUtils.parseIso8601ToMillis(article.publishedAt) }
                
                Log.d(TAG, "✨ Successfully parsed ${articles.size} articles for search query: $query")
                emit(Resource.Success(articles))
            } else {
                val errorMsg = "Search API returned status: ${response.status}"
                Log.e(TAG, "❌ $errorMsg")
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: e.message ?: "An unexpected error occurred"
            Log.e(TAG, "💥 Search Exception occurred: $errorMsg", e)
            emit(Resource.Error(errorMsg))
        }
    }

    override suspend fun getBookmarkedArticles(): Flow<List<Article>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration = firestore.collection("users")
            .document(userId)
            .collection("bookmarks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "⚠️ Error listening to bookmarks: ${error.message}", error)
                    trySend(emptyList()).isSuccess
                    return@addSnapshotListener
                }

                val articles = snapshot?.documents?.mapNotNull { doc ->
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
                        Log.w(TAG, "⚠️ Failed to map bookmarked article: ${e.message}")
                        null
                    }
                } ?: emptyList()

                trySend(articles).isSuccess
            }

        awaitClose { registration.remove() }
    }.distinctUntilChanged()

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

    /**
     * Optimized: Fetch all bookmarked article IDs at once
     * This is much faster than checking each article individually
     */
    private suspend fun getBookmarkedUrlsSet(): Set<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return emptySet()

            val snapshot = firestore
                .collection("users")
                .document(userId)
                .collection("bookmarks")
                .get()
                .await()

            snapshot.documents.map { it.id }.toSet()
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Error fetching bookmarks: ${e.message}")
            emptySet()
        }
    }

}