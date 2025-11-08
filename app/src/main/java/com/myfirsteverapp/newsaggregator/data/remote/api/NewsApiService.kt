//package com.myfirsteverapp.newsaggregator.data.remote.api
//
//import com.myfirsteverapp.newsaggregator.data.remote.dto.NewsResponseDto
//import retrofit2.http.GET
//import retrofit2.http.Query
//
//interface NewsApiService {
//
//    // Existing endpoints
//    @GET("top-headlines")
//    suspend fun getTopHeadlines(
//        @Query("country") country: String = "us",
//        @Query("category") category: String? = null,
//        @Query("apiKey") apiKey: String,
//        @Query("pageSize") pageSize: Int = 20,
//        @Query("page") page: Int = 1
//    ): NewsResponseDto
//
//    @GET("everything")
//    suspend fun searchNews(
//        @Query("q") query: String,
//        @Query("apiKey") apiKey: String,
//        @Query("language") language: String = "en",
//        @Query("sortBy") sortBy: String = "publishedAt",
//        @Query("pageSize") pageSize: Int = 20,
//        @Query("page") page: Int = 1
//    ): NewsResponseDto
//
//    @GET("everything")
//    suspend fun getNewsByCategory(
//        @Query("q") category: String,
//        @Query("apiKey") apiKey: String,
//        @Query("pageSize") pageSize: Int = 20
//    ): NewsResponseDto
//
//    // NEW: Add the specific endpoints you requested
//    @GET("everything")
//    suspend fun getEverythingNews(
//        @Query("q") query: String,
//        @Query("from") from: String,
//        @Query("sortBy") sortBy: String = "popularity",
//        @Query("apiKey") apiKey: String,
//        @Query("pageSize") pageSize: Int = 20,
//        @Query("page") page: Int = 1
//    ): NewsResponseDto
//
//    @GET("top-headlines")
//    suspend fun getTopHeadlinesByCountry(
//        @Query("country") country: String = "us",
//        @Query("apiKey") apiKey: String,
//        @Query("pageSize") pageSize: Int = 20,
//        @Query("page") page: Int = 1
//    ): NewsResponseDto
//
//    @GET("top-headlines")
//    suspend fun getTopHeadlinesBySource(
//        @Query("sources") sources: String,
//        @Query("apiKey") apiKey: String,
//        @Query("pageSize") pageSize: Int = 20,
//        @Query("page") page: Int = 1
//    ): NewsResponseDto
//
//    @GET("v2/top-headlines")
//    suspend fun getTopHeadlines(
//        @Query("country") country: String = "us",
//        @Query("category") category: String? = null,
//        @Query("pageSize") pageSize: Int = 20,
//        @Query("page") page: Int = 1
//    ): NewsResponseDto
//
//    @GET("v2/everything")
//    suspend fun searchNews(
//        @Query("q") query: String,
//        @Query("sortBy") sortBy: String = "publishedAt",
//        @Query("pageSize") pageSize: Int = 20,
//        @Query("page") page: Int = 1,
//        @Query("language") language: String = "en"
//    ): NewsResponseDto
//
//    @GET("v2/everything")
//    suspend fun getNewsBySource(
//        @Query("sources") sources: String,
//        @Query("pageSize") pageSize: Int = 20,
//        @Query("page") page: Int = 1
//    ): NewsResponseDto
//}


package com.myfirsteverapp.newsaggregator.data.remote

import com.myfirsteverapp.newsaggregator.data.remote.dto.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("category") category: String? = null,
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): NewsResponseDto

    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en"
    ): NewsResponseDto

    @GET("v2/everything")
    suspend fun getNewsBySource(
        @Query("sources") sources: String,
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): NewsResponseDto
}