package com.myfirsteverapp.newsaggregator.presentation.screens.home

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

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadNews()
    }

    fun loadNews(category: Category = Category.ALL) {
        viewModelScope.launch {
            newsRepository.getTopHeadlines(category).collect { resource ->
                // Demonstrates when expression
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true,
                                error = null,
                                isRefreshing = false
                            )
                        }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                articles = resource.data ?: emptyList(),
                                isLoading = false,
                                error = null,
                                isRefreshing = false
                            )
                        }
                    }
                    is Resource.Error -> {
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
        _uiState.update { it.copy(selectedCategory = category) }
        loadNews(category)
    }

    fun toggleBookmark(article: Article) {
        viewModelScope.launch {
            if (article.isBookmarked) {
                savedArticlesRepository.deleteArticle(article.id)
            } else {
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
        _uiState.update { it.copy(isRefreshing = true) }
        loadNews(_uiState.value.selectedCategory)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}