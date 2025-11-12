package com.myfirsteverapp.newsaggregator.presentation.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfirsteverapp.newsaggregator.domain.repository.NewsRepository
import com.myfirsteverapp.newsaggregator.data.repository.SavedArticlesRepository
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.domain.model.Category
import com.myfirsteverapp.newsaggregator.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: Category = Category.ALL,
    val isRefreshing: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val savedArticlesRepository: SavedArticlesRepository
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "🏁 HomeViewModel initialized - loading news...")
        loadNews()
    }

    fun loadNews(category: Category = Category.ALL) {
        viewModelScope.launch {
            Log.d(TAG, "🔄 loadNews called - Category: $category")

            val categoryParam = when (category) {
                Category.ALL -> null
                else -> category.name.lowercase()
            }

            // Optimize: Only show loading if we don't have articles already (for better UX)
            val currentArticles = _uiState.value.articles
            if (currentArticles.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        error = null,
                        isRefreshing = false
                    )
                }
            }

            try {
                newsRepository.getTopHeadlines(categoryParam).collect { resource ->
                    Log.d(TAG, "📦 Resource received: ${resource::class.simpleName}")

                    when (resource) {
                        is Resource.Loading -> {
                            Log.d(TAG, "⏳ Loading state")
                            // Only update loading state if we don't have cached articles
                            if (currentArticles.isEmpty()) {
                                _uiState.update {
                                    it.copy(
                                        isLoading = true,
                                        error = null,
                                        isRefreshing = false
                                    )
                                }
                            }
                        }
                        is Resource.Success -> {
                            val articleCount = resource.data?.size ?: 0
                            Log.d(TAG, "✅ Success! Articles received: $articleCount")

                            if (articleCount > 0) {
                                Log.d(TAG, "📰 First article: ${resource.data?.first()?.title}")
                            } else {
                                Log.w(TAG, "⚠️ Success but 0 articles received!")
                            }

                            _uiState.update {
                                it.copy(
                                    articles = resource.data ?: emptyList(),
                                    isLoading = false,
                                    error = null,
                                    isRefreshing = false,
                                    selectedCategory = category
                                )
                            }

                            Log.d(TAG, "📊 UI State updated - articles in state: ${_uiState.value.articles.size}")
                        }
                        is Resource.Error -> {
                            Log.e(TAG, "❌ Error: ${resource.message}")
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = resource.message,
                                    isRefreshing = false
                                )
                            }
                        }
                        else -> {
                            // Handle any other possible states
                            Log.w(TAG, "❓ Unknown resource state: $resource")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "💥 Exception in loadNews: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Failed to load news",
                        isRefreshing = false
                    )
                }
            }
        }
    }

    fun onCategorySelected(category: Category) {
        Log.d(TAG, "🏷️ Category selected: $category")
        _uiState.update { it.copy(selectedCategory = category) }
        loadNews(category)
    }

    fun toggleBookmark(article: Article) {
        Log.d(TAG, "🔖 Toggle bookmark for: ${article.title}")
        viewModelScope.launch {
            if (article.isBookmarked) {
                Log.d(TAG, "➖ Removing bookmark")
                newsRepository.removeBookmark(article)
            } else {
                Log.d(TAG, "➕ Adding bookmark")
                newsRepository.addBookmark(article)
            }

            // Update local state optimistically
            _uiState.update { state ->
                state.copy(
                    articles = state.articles.map {
                        if (it.url == article.url) {
                            it.copy(isBookmarked = !it.isBookmarked)
                        } else it
                    }
                )
            }
        }
    }

    fun refresh() {
        Log.d(TAG, "🔄 Refresh triggered")
        _uiState.update { it.copy(isRefreshing = true) }
        loadNews(_uiState.value.selectedCategory)
    }

    fun clearError() {
        Log.d(TAG, "🧹 Clearing error")
        _uiState.update { it.copy(error = null) }
    }
}