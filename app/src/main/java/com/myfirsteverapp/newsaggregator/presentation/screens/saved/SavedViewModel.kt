package com.myfirsteverapp.newsaggregator.presentation.screens.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.domain.repository.NewsRepository
import com.myfirsteverapp.newsaggregator.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _savedArticles = MutableStateFlow<Resource<List<Article>>>(Resource.Loading())
    val savedArticles: StateFlow<Resource<List<Article>>> = _savedArticles.asStateFlow()

    init {
        loadSavedArticles()
    }

    fun loadSavedArticles() {
        viewModelScope.launch {
            try {
                _savedArticles.value = Resource.Loading()
                newsRepository.getBookmarkedArticles().collect { articles ->
                    _savedArticles.value = Resource.Success(articles)
                }
            } catch (e: Exception) {
                _savedArticles.value = Resource.Error(e.localizedMessage ?: "Failed to load saved articles")
            }
        }
    }

    fun toggleBookmark(article: Article) {
        viewModelScope.launch {
            newsRepository.removeBookmark(article)

            // Update the list immediately
            _savedArticles.value = when (val current = _savedArticles.value) {
                is Resource.Success -> {
                    val updatedList = current.data?.filter { it.url != article.url } ?: emptyList()
                    Resource.Success(updatedList)
                }
                else -> current
            }
        }
    }
}