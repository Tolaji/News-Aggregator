package com.myfirsteverapp.newsaggregator.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.domain.repository.NewsRepository
import com.myfirsteverapp.newsaggregator.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<Resource<List<Article>>>(Resource.Idle())
    val searchResults: StateFlow<Resource<List<Article>>> = _searchResults.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    init {
        loadRecentSearches()

        // Auto-search with debounce
        viewModelScope.launch {
            searchQuery
                .debounce(500) // Wait 500ms after user stops typing
                .filter { it.length >= 3 } // Only search if query is 3+ chars
                .distinctUntilChanged()
                .collectLatest { query ->
                    search(query)
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            _searchResults.value = Resource.Idle()
        }
    }

    fun search(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _searchResults.value = Resource.Loading()

            newsRepository.searchNews(query).collect { resource ->
                _searchResults.value = resource

                // Save to recent searches on success
                if (resource is Resource.Success) {
                    saveRecentSearch(query)
                }
            }
        }
    }

    fun toggleBookmark(article: Article) {
        viewModelScope.launch {
            if (article.isBookmarked) {
                newsRepository.removeBookmark(article)
            } else {
                newsRepository.addBookmark(article)
            }

            // Update the article in current results
            _searchResults.value = when (val current = _searchResults.value) {
                is Resource.Success -> {
                    val updatedList = current.data?.map {
                        if (it.url == article.url) {
                            it.copy(isBookmarked = !it.isBookmarked)
                        } else it
                    }
                    Resource.Success(updatedList)
                }
                else -> current
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = Resource.Idle()
    }

    private fun saveRecentSearch(query: String) {
        viewModelScope.launch {
            val current = _recentSearches.value.toMutableList()

            // Remove if already exists
            current.remove(query)

            // Add to beginning
            current.add(0, query)

            // Keep only last 10 searches
            if (current.size > 10) {
                current.removeAt(current.size - 1)
            }

            _recentSearches.value = current

            // TODO: Persist to DataStore for long-term storage
        }
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            // TODO: Load from DataStore
            // For now, using in-memory storage
            _recentSearches.value = emptyList()
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            _recentSearches.value = emptyList()
            // TODO: Clear from DataStore
        }
    }
}