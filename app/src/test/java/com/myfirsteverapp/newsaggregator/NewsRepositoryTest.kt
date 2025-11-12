package com.myfirsteverapp.newsaggregator

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.myfirsteverapp.newsaggregator.data.remote.api.NewsApiService
import com.myfirsteverapp.newsaggregator.data.remote.dto.ArticleDto
import com.myfirsteverapp.newsaggregator.data.remote.dto.NewsResponseDto
import com.myfirsteverapp.newsaggregator.data.remote.dto.SourceDto
import com.myfirsteverapp.newsaggregator.data.repository.NewsRepositoryImpl
import com.myfirsteverapp.newsaggregator.util.Resource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Fixed unit tests with proper Firebase mocking
 */
class NewsRepositoryTest {

    private lateinit var newsApi: NewsApiService
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var mockUser: FirebaseUser
    private lateinit var repository: NewsRepositoryImpl

    @Before
    fun setup() {
        newsApi = mock()
        firestore = mock()
        auth = mock()
        mockUser = mock()

        // Mock Firebase Auth to return null user (not authenticated)
        whenever(auth.currentUser).thenReturn(null)

        repository = NewsRepositoryImpl(newsApi, firestore, auth)
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
                    urlToImage = "https://test.com/image.jpg",
                    publishedAt = "2024-01-01T00:00:00Z",
                    content = "Test Content"
                )
            )
        )

        whenever(
            newsApi.getTopHeadlines(
                country = any(),
                category = any(),
                pageSize = any(),
                page = any()
            )
        ).thenReturn(mockResponse)

        // When
        val result = repository.getTopHeadlines(category = null).first()

        // Then
        assertTrue(result is Resource.Success)
        val articles = (result as Resource.Success).data
        assertEquals(1, articles?.size)
        assertEquals("Test Title", articles?.first()?.title)
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
                    urlToImage = "https://test.com/search.jpg",
                    publishedAt = "2024-01-01T00:00:00Z",
                    content = "Search content"
                )
            )
        )

        whenever(
            newsApi.searchNews(
                query = any(),
                sortBy = any(),
                pageSize = any(),
                page = any(),
                language = any()
            )
        ).thenReturn(mockResponse)

        // When
        val result = repository.searchNews("test query").first()

        // Then
        assertTrue(result is Resource.Success)
        val articles = (result as Resource.Success).data
        assertEquals(1, articles?.size)
        assertEquals("Search Result", articles?.first()?.title)
    }

    @Test
    fun `getTopHeadlines returns error on exception`() = runTest {
        // Given
        whenever(
            newsApi.getTopHeadlines(
                country = any(),
                category = any(),
                pageSize = any(),
                page = any()
            )
        ).thenThrow(RuntimeException("Network error"))

        // When
        val result = repository.getTopHeadlines(category = null).first()

        // Then
        assertTrue(result is Resource.Error)
        val errorMessage = (result as Resource.Error).message
        assertTrue(errorMessage?.contains("error") == true)
    }

    @Test
    fun `getTopHeadlines filters out articles with missing required fields`() = runTest {
        // Given - response with one valid and one invalid article
        val mockResponse = NewsResponseDto(
            status = "ok",
            totalResults = 2,
            articles = listOf(
                ArticleDto(
                    source = SourceDto(null, "Valid Source"),
                    author = "Author",
                    title = "Valid Title",
                    description = "Description",
                    url = "https://test.com/valid",
                    urlToImage = "https://test.com/valid.jpg",
                    publishedAt = "2024-01-01T00:00:00Z",
                    content = "Content"
                ),
                ArticleDto(
                    source = SourceDto(null, "Invalid Source"),
                    author = "Author",
                    title = null, // MISSING TITLE
                    description = "Description",
                    url = "https://test.com/invalid",
                    urlToImage = null,
                    publishedAt = "2024-01-01T00:00:00Z",
                    content = "Content"
                )
            )
        )

        whenever(
            newsApi.getTopHeadlines(
                country = any(),
                category = any(),
                pageSize = any(),
                page = any()
            )
        ).thenReturn(mockResponse)

        // When
        val result = repository.getTopHeadlines(category = null).first()

        // Then - should only return the valid article
        assertTrue(result is Resource.Success)
        val articles = (result as Resource.Success).data
        assertEquals(1, articles?.size)
        assertEquals("Valid Title", articles?.first()?.title)
    }

    @Test
    fun `searchNews with empty query still makes API call`() = runTest {
        // Given
        val mockResponse = NewsResponseDto(
            status = "ok",
            totalResults = 0,
            articles = emptyList()
        )

        whenever(
            newsApi.searchNews(
                query = any(),
                sortBy = any(),
                pageSize = any(),
                page = any(),
                language = any()
            )
        ).thenReturn(mockResponse)

        // When
        val result = repository.searchNews("").first()

        // Then
        assertTrue(result is Resource.Success)
        val articles = (result as Resource.Success).data
        assertEquals(0, articles?.size)
    }
}