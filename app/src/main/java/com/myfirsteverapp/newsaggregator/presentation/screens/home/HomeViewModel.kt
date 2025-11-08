//package com.myfirsteverapp.newsaggregator.presentation.screens.home
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.myfirsteverapp.newsaggregator.data.repository.NewsRepository
//import com.myfirsteverapp.newsaggregator.data.repository.SavedArticlesRepository
//import com.myfirsteverapp.newsaggregator.domain.model.Article
//import com.myfirsteverapp.newsaggregator.domain.model.Category
//import com.myfirsteverapp.newsaggregator.util.Resource
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//data class HomeUiState(
//    val articles: List<Article> = emptyList(),
//    val isLoading: Boolean = false,
//    val error: String? = null,
//    val selectedCategory: Category = Category.ALL,
//    val isRefreshing: Boolean = false
//)
//
//@HiltViewModel
//class HomeViewModel @Inject constructor(
//    private val newsRepository: NewsRepository,
//    private val savedArticlesRepository: SavedArticlesRepository
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(HomeUiState())
//    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
//
//    init {
//        loadNews()
//    }
//
//    fun loadNews(category: Category = Category.ALL) {
//        viewModelScope.launch {
//            newsRepository.getTopHeadlines(category).collect { resource ->
//                // Demonstrates when expression
//                when (resource) {
//                    is Resource.Loading -> {
//                        _uiState.update {
//                            it.copy(
//                                isLoading = true,
//                                error = null,
//                                isRefreshing = false
//                            )
//                        }
//                    }
//                    is Resource.Success -> {
//                        _uiState.update {
//                            it.copy(
//                                articles = resource.data ?: emptyList(),
//                                isLoading = false,
//                                error = null,
//                                isRefreshing = false
//                            )
//                        }
//                    }
//                    is Resource.Error -> {
//                        _uiState.update {
//                            it.copy(
//                                isLoading = false,
//                                error = resource.message,
//                                isRefreshing = false
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    fun onCategorySelected(category: Category) {
//        _uiState.update { it.copy(selectedCategory = category) }
//        loadNews(category)
//    }
//
//    fun toggleBookmark(article: Article) {
//        viewModelScope.launch {
//            if (article.isBookmarked) {
//                savedArticlesRepository.deleteArticle(article.id)
//            } else {
//                savedArticlesRepository.saveArticle(article.copy(isBookmarked = true))
//            }
//
//            // Update local state optimistically
//            _uiState.update { state ->
//                state.copy(
//                    articles = state.articles.map {
//                        if (it.id == article.id) {
//                            it.copy(isBookmarked = !it.isBookmarked)
//                        } else it
//                    }
//                )
//            }
//        }
//    }
//
//    fun refresh() {
//        _uiState.update { it.copy(isRefreshing = true) }
//        loadNews(_uiState.value.selectedCategory)
//    }
//
//    fun clearError() {
//        _uiState.update { it.copy(error = null) }
//    }
//}

package com.myfirsteverapp.newsaggregator.presentation.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfirsteverapp.newsaggregator.data.repository.NewsRepository
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

            newsRepository.getTopHeadlines(category).collect { resource ->
                Log.d(TAG, "📦 Resource received: ${resource::class.simpleName}")

                when (resource) {
                    is Resource.Loading -> {
                        Log.d(TAG, "⏳ Loading state")
                        _uiState.update {
                            it.copy(
                                isLoading = true,
                                error = null,
                                isRefreshing = false
                            )
                        }
                    }
                    is Resource.Success -> {
                        val articleCount = resource.data?.size ?: 0
                        Log.d(TAG, "✅ Success! Articles received: $articleCount")

                        if (articleCount > 0) {
                            Log.d(TAG, "📰 First article: ${resource.data?.first()?.title}")
                            Log.d(TAG, "📰 Sample article IDs: ${resource.data?.take(3)?.map { it.id }}")
                        } else {
                            Log.w(TAG, "⚠️ Success but 0 articles received!")
                        }

                        _uiState.update {
                            it.copy(
                                articles = resource.data ?: emptyList(),
                                isLoading = false,
                                error = null,
                                isRefreshing = false
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
                savedArticlesRepository.deleteArticle(article.id)
            } else {
                Log.d(TAG, "➕ Adding bookmark")
                savedArticlesRepository.saveArticle(article.copy(isBookmarked = true))
            }

            // Update local state optimistically
            _uiState.update { state ->
                state.copy(
                    articles = state.articles.map {
                        if (it.id == article.id) {
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