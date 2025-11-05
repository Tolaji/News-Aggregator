package com.myfirsteverapp.newsaggregator.data.mapper

import com.myfirsteverapp.newsaggregator.data.remote.dto.ArticleDto
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.domain.model.Category
import com.myfirsteverapp.newsaggregator.domain.model.Source
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// Mapper class responsible for converting data transfer objects (DTOs)
// from the network layer into domain models used within the app.
class ArticleMapper @Inject constructor() {

    // Define a date format for parsing the "publishedAt" field from the API.
    // The format follows ISO 8601 and assumes UTC time zone.
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    // Function to convert an ArticleDto into a domain-level Article object.
    // Takes in the DTO and a Category enum/value.
    fun mapDtoToDomain(dto: ArticleDto, category: Category): Article {
        // Generate a unique ID for each article using its URL’s hash code.
        // This ensures a consistent identifier even when the API doesn’t provide one.
        val id = dto.url.hashCode().toString()

        // Build and return the domain Article object.
        return Article(
            id = id,
            title = dto.title,
            // Use empty string if description is null to prevent crashes.
            description = dto.description ?: "",
            // Fallback to description if content is missing.
            content = dto.content ?: dto.description ?: "",
            url = dto.url,
            imageUrl = dto.imageUrl,
            author = dto.author,
            // Map the nested source DTO to the domain Source model.
            source = Source(
                id = dto.source.id,
                name = dto.source.name
            ),
            // Try parsing the published date; fallback to current date if parsing fails.
            publishedAt = try {
                dateFormat.parse(dto.publishedAt) ?: Date()
            } catch (e: Exception) {
                Date()
            },
            // Attach the provided category to the article.
            category = category
        )
    }
}
