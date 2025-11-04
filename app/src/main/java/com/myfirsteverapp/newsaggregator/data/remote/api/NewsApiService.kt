package com.myfirsteverapp.newsaggregator.data.remote.api

import com.myfirsteverapp.newsaggregator.data.remote.dto.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("category") category: String? = null,
        @Query("apiKey") apiKey: String,
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): NewsResponseDto

    @GET("everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String,
        @Query("language") language: String = "en",
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): NewsResponseDto

    @GET("everything")
    suspend fun getNewsByCategory(
        @Query("q") category: String,
        @Query("apiKey") apiKey: String,
        @Query("pageSize") pageSize: Int = 20
    ): NewsResponseDto
}