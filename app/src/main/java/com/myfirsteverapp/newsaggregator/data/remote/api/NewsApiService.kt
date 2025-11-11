package com.myfirsteverapp.newsaggregator.data.remote.api

import com.myfirsteverapp.newsaggregator.data.remote.dto.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    /**
     * FIXED: Removed duplicate /v2/ prefix from endpoints
     * The base URL already includes /v2/, so endpoints should start directly
     */

    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("category") category: String? = null,
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): NewsResponseDto

    @GET("everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en"
    ): NewsResponseDto

    @GET("everything")
    suspend fun getNewsBySource(
        @Query("sources") sources: String,
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): NewsResponseDto
}