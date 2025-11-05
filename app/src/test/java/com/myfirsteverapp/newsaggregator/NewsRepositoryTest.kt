// File: `app/src/test/java/com/myfirsteverapp/newsaggregator/NewsRepositoryTest.kt`
package com.myfirsteverapp.newsaggregator

import com.myfirsteverapp.newsaggregator.data.mapper.ArticleMapper
import com.myfirsteverapp.newsaggregator.data.remote.api.NewsApiService
import com.myfirsteverapp.newsaggregator.data.remote.dto.ArticleDto
import com.myfirsteverapp.newsaggregator.data.remote.dto.NewsResponseDto
import com.myfirsteverapp.newsaggregator.data.remote.dto.SourceDto
import com.myfirsteverapp.newsaggregator.data.repository.NewsRepository
import com.myfirsteverapp.newsaggregator.domain.model.Category
import com.myfirsteverapp.newsaggregator.util.Resource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class NewsRepositoryTest {

    private val newsApi: NewsApiService = mock()
    private lateinit var repository: NewsRepository
    private lateinit var mapper: ArticleMapper

    @Before
    fun setup() {
        mapper = ArticleMapper()
        repository = NewsRepository(newsApi, mapper)
    }

    @Test
    fun `getTopHeadlines returns success with articles`() = runTest {
        // Given
        val mockResponse = NewsResponseDto(
            status = "ok",
            totalResults = 1,
            articles = listOf(
                ArticleDto(
                    source = SourceDto(null, "Test Source"),
                    author = "Test Author",
                    title = "Test Title",
                    description = "Test Description",
                    url = "https://test.com",
                    imageUrl = null,
                    publishedAt = "2024-01-01T00:00:00Z",
                    content = "Test Content"
                )
            )
        )

        whenever(newsApi.getTopHeadlines(
            country = "us",
            category = anyOrNull(),
            apiKey = any(),
            pageSize = any(),
            page = any()
        )).thenReturn(mockResponse)

        // When
        val result = repository.getTopHeadlines(Category.ALL).first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
        assertEquals("Test Title", result.data?.first()?.title)
    }

    @Test
    fun `getEverythingNews returns success with articles`() = runTest {
        // Given
        val mockResponse = NewsResponseDto(
            status = "ok",
            totalResults = 1,
            articles = listOf(
                ArticleDto(
                    source = SourceDto(null, "Test Source"),
                    author = "Test Author",
                    title = "Apple News",
                    description = "Apple product launch",
                    url = "https://test.com/apple",
                    imageUrl = null,
                    publishedAt = "2024-01-01T00:00:00Z",
                    content = "Apple content"
                )
            )
        )

        whenever(newsApi.getEverythingNews(
            query = any(),
            from = any(),
            sortBy = any(),
            apiKey = any(),
            pageSize = any(),
            page = any()
        )).thenReturn(mockResponse)

        // When
        val result = repository.getEverythingNews().first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
        assertEquals("Apple News", result.data?.first()?.title)
    }

    @Test
    fun `getTopHeadlinesByCountry returns success with articles`() = runTest {
        // Given
        val mockResponse = NewsResponseDto(
            status = "ok",
            totalResults = 1,
            articles = listOf(
                ArticleDto(
                    source = SourceDto(null, "US News"),
                    author = "US Author",
                    title = "US Headline",
                    description = "US news description",
                    url = "https://test.com/us",
                    imageUrl = null,
                    publishedAt = "2024-01-01T00:00:00Z",
                    content = "US content"
                )
            )
        )

        whenever(newsApi.getTopHeadlinesByCountry(
            country = any(),
            apiKey = any(),
            pageSize = any(),
            page = any()
        )).thenReturn(mockResponse)

        // When
        val result = repository.getTopHeadlinesByCountry("us").first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
        assertEquals("US Headline", result.data?.first()?.title)
    }

    @Test
    fun `getTopHeadlinesBySource returns success with articles`() = runTest {
        // Given
        val mockResponse = NewsResponseDto(
            status = "ok",
            totalResults = 1,
            articles = listOf(
                ArticleDto(
                    source = SourceDto("bbc-news", "BBC News"),
                    author = "BBC Author",
                    title = "BBC Headline",
                    description = "BBC news description",
                    url = "https://test.com/bbc",
                    imageUrl = null,
                    publishedAt = "2024-01-01T00:00:00Z",
                    content = "BBC content"
                )
            )
        )

        whenever(newsApi.getTopHeadlinesBySource(
            sources = any(),
            apiKey = any(),
            pageSize = any(),
            page = any()
        )).thenReturn(mockResponse)

        // When
        val result = repository.getTopHeadlinesBySource("bbc-news").first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
        assertEquals("BBC Headline", result.data?.first()?.title)
    }

    @Test
    fun `searchNews returns success with articles`() = runTest {
        // Given
        val mockResponse = NewsResponseDto(
            status = "ok",
            totalResults = 1,
            articles = listOf(
                ArticleDto(
                    source = SourceDto(null, "Search Source"),
                    author = "Search Author",
                    title = "Search Result",
                    description = "Search description",
                    url = "https://test.com/search",
                    imageUrl = null,
                    publishedAt = "2024-01-01T00:00:00Z",
                    content = "Search content"
                )
            )
        )

        whenever(newsApi.searchNews(
            query = any(),
            apiKey = any(),
            language = any(),
            sortBy = any(),
            pageSize = any(),
            page = any()
        )).thenReturn(mockResponse)

        // When
        val result = repository.searchNews("test query").first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
        assertEquals("Search Result", result.data?.first()?.title)
    }

    @Test
    fun `getTopHeadlines returns error on exception`() = runTest {
        // Given
        whenever(newsApi.getTopHeadlines(
            country = any(),
            category = anyOrNull(),
            apiKey = any(),
            pageSize = any(),
            page = any()
        )).thenThrow(RuntimeException("Network error"))

        // When
        val result = repository.getTopHeadlines(Category.ALL).first()

        // Then
        assertTrue(result is Resource.Error)
        assertEquals("Network error", (result as Resource.Error).message)
    }

    @Test
    fun `category detection works correctly`() = runTest {
        // Given - article with business-related content
        val mockResponse = NewsResponseDto(
            status = "ok",
            totalResults = 1,
            articles = listOf(
                ArticleDto(
                    source = SourceDto(null, "Business Source"),
                    author = "Business Author",
                    title = "Stock Market News",
                    description = "Finance and market updates",
                    url = "https://test.com/business",
                    imageUrl = null,
                    publishedAt = "2024-01-01T00:00:00Z",
                    content = "Stock market reached new highs today"
                )
            )
        )

        whenever(newsApi.getTopHeadlinesByCountry(
            country = any(),
            apiKey = any(),
            pageSize = any(),
            page = any()
        )).thenReturn(mockResponse)

        // When
        val result = repository.getTopHeadlinesByCountry("us").first()

        // Then - should detect BUSINESS category
        assertTrue(result is Resource.Success)
        val article = (result as Resource.Success).data?.first()
        assertEquals(Category.BUSINESS, article?.category)
    }

    @Test
    fun `getTopHeadlines with matchers returns success with articles`() = runTest {
        // Given
        val mockResponse = NewsResponseDto(
            status = "ok",
            totalResults = 1,
            articles = listOf(
                ArticleDto(
                    source = SourceDto(null, "Test Source"),
                    author = "Test Author",
                    title = "Test Title",
                    description = "Test Description",
                    url = "https://test.com",
                    imageUrl = null,
                    publishedAt = "2024-01-01T00:00:00Z",
                    content = "Test Content"
                )
            )
        )

        // Use matchers only for parameters you don't care about
        whenever(
            newsApi.getTopHeadlines(
                country = org.mockito.kotlin.eq("us"),
                category = org.mockito.kotlin.eq(null),
                apiKey = org.mockito.kotlin.any(),
                pageSize = org.mockito.kotlin.any(),
                page = org.mockito.kotlin.any()
            )
        ).thenReturn(mockResponse)

        // When
        val result = repository.getTopHeadlines(Category.ALL).first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
    }
}