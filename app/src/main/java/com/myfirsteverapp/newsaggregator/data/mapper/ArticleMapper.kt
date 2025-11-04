package com.myfirsteverapp.newsaggregator.data.mapper

import com.myfirsteverapp.newsaggregator.data.remote.dto.ArticleDto
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.domain.model.Category
import com.myfirsteverapp.newsaggregator.domain.model.Source
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ArticleMapper @Inject constructor() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun mapDtoToDomain(dto: ArticleDto, category: Category): Article {
        // Generate unique ID from URL
        val id = dto.url.hashCode().toString()

        return Article(
            id = id,
            title = dto.title,
            description = dto.description ?: "",
            content = dto.content ?: dto.description ?: "",
            url = dto.url,
            imageUrl = dto.imageUrl,
            author = dto.author,
            source = Source(
                id = dto.source.id,
                name = dto.source.name
            ),
            publishedAt = try {
                dateFormat.parse(dto.publishedAt) ?: Date()
            } catch (e: Exception) {
                Date()
            },
            category = category
        )
    }
}