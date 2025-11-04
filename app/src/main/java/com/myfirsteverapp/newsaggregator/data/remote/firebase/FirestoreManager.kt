package com.myfirsteverapp.newsaggregator.data.remote.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.domain.model.User
import com.myfirsteverapp.newsaggregator.domain.model.UserPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authManager: FirebaseAuthManager
) {

    private fun getUserId(): String {
        return authManager.getCurrentUserId()
            ?: throw IllegalStateException("User not authenticated")
    }

    // ==================== CREATE (INSERT) ====================

    suspend fun saveArticle(article: Article): Result<Unit> {
        return try {
            val userId = getUserId()
            val articleData = hashMapOf(
                "id" to article.id,
                "title" to article.title,
                "description" to article.description,
                "content" to article.content,
                "url" to article.url,
                "imageUrl" to article.imageUrl,
                "author" to article.author,
                "sourceName" to article.source.name,
                "publishedAt" to Timestamp(article.publishedAt),
                "category" to article.category.name,
                "savedAt" to Timestamp.now(),
                "isBookmarked" to true
            )

            firestore.collection("users")
                .document(userId)
                .collection("savedArticles")
                .document(article.id)
                .set(articleData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUserProfile(user: User): Result<Unit> {
        return try {
            val userData = hashMapOf(
                "uid" to user.uid,
                "email" to user.email,
                "displayName" to user.displayName,
                "photoUrl" to user.photoUrl,
                "createdAt" to Timestamp(java.util.Date(user.createdAt))
            )

            firestore.collection("users")
                .document(user.uid)
                .set(userData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== READ (QUERY) ====================

    fun getSavedArticles(): Flow<Result<List<Article>>> = callbackFlow {
        val userId = getUserId()

        val listener = firestore.collection("users")
            .document(userId)
            .collection("savedArticles")
            .orderBy("savedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                val articles = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        // Map Firestore document to Article domain model
                        Article(
                            id = doc.getString("id") ?: "",
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            content = doc.getString("content") ?: "",
                            url = doc.getString("url") ?: "",
                            imageUrl = doc.getString("imageUrl"),
                            author = doc.getString("author"),
                            source = com.myfirsteverapp.newsaggregator.domain.model.Source(
                                id = null,
                                name = doc.getString("sourceName") ?: ""
                            ),
                            publishedAt = doc.getTimestamp("publishedAt")?.toDate()
                                ?: java.util.Date(),
                            category = com.myfirsteverapp.newsaggregator.domain.model.Category.valueOf(
                                doc.getString("category") ?: "ALL"
                            ),
                            isBookmarked = true
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(Result.success(articles))
            }

        awaitClose { listener.remove() }
    }

    suspend fun getUserPreferences(): Result<UserPreferences> {
        return try {
            val userId = getUserId()
            val doc = firestore.collection("users")
                .document(userId)
                .collection("preferences")
                .document("settings")
                .get()
                .await()

            if (doc.exists()) {
                val prefs = UserPreferences(
                    favoriteCategories = (doc.get("favoriteCategories") as? List<String>)
                        ?.mapNotNull {
                            try {
                                com.myfirsteverapp.newsaggregator.domain.model.Category.valueOf(it)
                            } catch (e: Exception) { null }
                        } ?: emptyList(),
                    notificationsEnabled = doc.getBoolean("notificationsEnabled") ?: true,
                    darkModeEnabled = doc.getBoolean("darkModeEnabled") ?: false,
                    autoRefresh = doc.getBoolean("autoRefresh") ?: true
                )
                Result.success(prefs)
            } else {
                Result.success(UserPreferences())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== UPDATE (MODIFY) ====================

    suspend fun updateArticleReadStatus(
        articleId: String,
        isRead: Boolean
    ): Result<Unit> {
        return try {
            val userId = getUserId()
            firestore.collection("users")
                .document(userId)
                .collection("savedArticles")
                .document(articleId)
                .update("readStatus", if (isRead) "READ" else "UNREAD")
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserPreferences(prefs: UserPreferences): Result<Unit> {
        return try {
            val userId = getUserId()
            val prefsData = hashMapOf(
                "favoriteCategories" to prefs.favoriteCategories.map { it.name },
                "notificationsEnabled" to prefs.notificationsEnabled,
                "darkModeEnabled" to prefs.darkModeEnabled,
                "autoRefresh" to prefs.autoRefresh,
                "updatedAt" to Timestamp.now()
            )

            firestore.collection("users")
                .document(userId)
                .collection("preferences")
                .document("settings")
                .set(prefsData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== DELETE ====================

    suspend fun deleteArticle(articleId: String): Result<Unit> {
        return try {
            val userId = getUserId()
            firestore.collection("users")
                .document(userId)
                .collection("savedArticles")
                .document(articleId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllSavedArticles(): Result<Unit> {
        return try {
            val userId = getUserId()
            val batch = firestore.batch()

            val articles = firestore.collection("users")
                .document(userId)
                .collection("savedArticles")
                .get()
                .await()

            articles.documents.forEach { doc ->
                batch.delete(doc.reference)
            }

            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== REAL-TIME NOTIFICATIONS ====================

    fun listenForNewArticles(category: String): Flow<Article?> = callbackFlow {
        val listener = firestore.collection("articles")
            .whereEqualTo("category", category)
            .orderBy("publishedAt", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                snapshot?.documentChanges?.forEach { change ->
                    if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                        // Parse and send new article
                        // Implementation similar to getSavedArticles mapping
                        trySend(null) // Placeholder
                    }
                }
            }

        awaitClose { listener.remove() }
    }
}