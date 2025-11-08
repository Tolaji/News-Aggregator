package com.myfirsteverapp.newsaggregator.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    fun toggleBookmark(article: Article) {
        viewModelScope.launch {
            if (article.isBookmarked) {
                newsRepository.removeBookmark(article)
            } else {
                newsRepository.addBookmark(article)
            }
        }
    }
}