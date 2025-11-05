package com.myfirsteverapp.newsaggregator.data.repository

import com.myfirsteverapp.newsaggregator.BuildConfig
import com.myfirsteverapp.newsaggregator.data.mapper.ArticleMapper
import com.myfirsteverapp.newsaggregator.data.remote.api.NewsApiService
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.domain.model.Category
import com.myfirsteverapp.newsaggregator.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val newsApi: NewsApiService,
    private val mapper: ArticleMapper
) {

    // Existing methods...
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

    fun getEverythingNews(
        query: String = "Apple",
        from: String = run {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -7)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.format(cal.time)
        },
        sortBy: String = "popularity",
        page: Int = 1
    ): Flow<Resource<List<Article>>> = flow {
        emit(Resource.Loading())
        try {
            val response = newsApi.getEverythingNews(
                query = query,
                from = from,
                sortBy = sortBy,
                apiKey = BuildConfig.NEWS_API_KEY,
                page = page
            )
            val articles = response.articles.map { dto ->
                mapper.mapDtoToDomain(dto, Category.ALL)
            }
            emit(Resource.Success(articles))
        } catch (e: Exception) {
            emit(Resource.Error(
                message = e.localizedMessage ?: "Failed to fetch everything news"
            ))
        }
    }

    fun getTopHeadlinesByCountry(
        country: String = "us",
        page: Int = 1
    ): Flow<Resource<List<Article>>> = flow {
        emit(Resource.Loading())
        try {
            val response = newsApi.getTopHeadlinesByCountry(
                country = country,
                apiKey = BuildConfig.NEWS_API_KEY,
                page = page
            )
            val articles = response.articles.map { dto ->
                val category = detectCategoryFromContent(
                    title = dto.title,
                    description = dto.description,
                    content = dto.content
                )
                mapper.mapDtoToDomain(dto, category)
            }
            emit(Resource.Success(articles))
        } catch (e: Exception) {
            emit(Resource.Error(
                message = e.localizedMessage ?: "Failed to fetch top headlines"
            ))
        }
    }

    fun getTopHeadlinesBySource(
        sources: String = "bbc-news",
        page: Int = 1
    ): Flow<Resource<List<Article>>> = flow {
        emit(Resource.Loading())
        try {
            val response = newsApi.getTopHeadlinesBySource(
                sources = sources,
                apiKey = BuildConfig.NEWS_API_KEY,
                page = page
            )
            val articles = response.articles.map { dto ->
                mapper.mapDtoToDomain(dto, Category.ALL)
            }
            emit(Resource.Success(articles))
        } catch (e: Exception) {
            emit(Resource.Error(
                message = e.localizedMessage ?: "Failed to fetch source headlines"
            ))
        }
    }

    // Helper function to detect category from content
    private fun detectCategoryFromContent(title: String?, description: String?, content: String?): Category {
        val text = listOf(title, description, content)
            .filterNotNull()
            .joinToString(" ")
            .lowercase()
            .trim()
        if (text.isBlank()) return Category.ALL

        // Match explicit category names from enum if present in text
        Category.values().forEach { category ->
            if (category == Category.ALL) return@forEach
            if (text.contains(category.name.lowercase())) return category
        }

        // Heuristic keyword -> enum name mapping (falls back to ALL)
        val heuristics = mapOf(
            listOf("business", "market", "stock", "finance", "company", "economy", "investment") to Category.BUSINESS,
            listOf("tech", "technology", "software", "ai", "startup", "digital", "computer", "internet") to Category.TECH,
            listOf("sport", "football", "soccer", "nba", "nfl", "cricket", "tennis", "basketball", "baseball") to Category.SPORTS,
            listOf("movie", "film", "music", "celebrity", "tv", "series", "entertainment", "hollywood") to Category.ENTERTAINMENT,
            listOf("health", "medical", "doctor", "hospital", "wellness", "disease", "medicine", "healthcare") to Category.HEALTH,
            listOf("science", "research", "space", "nasa", "discovery", "scientist", "experiment") to Category.SCIENCE,
            listOf("politics", "government", "election", "president", "congress", "senate", "political") to Category.POLITICS
        )

        heuristics.forEach { (keywords, category) ->
            if (keywords.any { text.contains(it) }) {
                return category
            }
        }

        return Category.ALL
    }
}