// Kotlin
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
import org.mockito.kotlin.whenever
import org.mockito.kotlin.mock

class NewsRepositoryTest {

    // create mock via org.mockito.kotlin.mock()
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
            category = null,
            apiKey = any(),
            page = 1
        )).thenReturn(mockResponse)

        // When
        val result = repository.getTopHeadlines(Category.ALL).first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
    }
}
