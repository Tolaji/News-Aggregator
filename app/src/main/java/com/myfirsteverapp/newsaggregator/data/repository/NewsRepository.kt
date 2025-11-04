package com.myfirsteverapp.newsaggregator.data.repository

import com.myfirsteverapp.newsaggregator.BuildConfig
import com.myfirsteverapp.newsaggregator.data.mapper.ArticleMapper
import com.myfirsteverapp.newsaggregator.data.remote.api.NewsApiService
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.domain.model.Category
import com.myfirsteverapp.newsaggregator.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val newsApi: NewsApiService,
    private val mapper: ArticleMapper
) {

    fun getTopHeadlines(
        category: Category = Category.ALL,
        page: Int = 1
    ): Flow<Resource<List<Article>>> = flow {
        emit(Resource.Loading())

        try {
            val categoryParam = Category.getApiCategory(category)
            val response = newsApi.getTopHeadlines(
                category = categoryParam,
                apiKey = BuildConfig.NEWS_API_KEY,
                page = page
            )

            // Demonstrates collections and loops
            val articles = response.articles.map { dto ->
                mapper.mapDtoToDomain(dto, category)
            }

            emit(Resource.Success(articles))
        } catch (e: Exception) {
            emit(Resource.Error(
                message = e.localizedMessage ?: "Failed to fetch news",
                data = null
            ))
        }
    }

    fun searchNews(query: String, page: Int = 1): Flow<Resource<List<Article>>> = flow {
        emit(Resource.Loading())

        try {
            val response = newsApi.searchNews(
                query = query,
                apiKey = BuildConfig.NEWS_API_KEY,
                page = page
            )

            val articles = response.articles.map { dto ->
                mapper.mapDtoToDomain(dto, Category.ALL)
            }

            emit(Resource.Success(articles))
        } catch (e: Exception) {
            emit(Resource.Error(
                message = e.localizedMessage ?: "Search failed"
            ))
        }
    }
}