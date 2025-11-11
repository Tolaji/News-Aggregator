package com.myfirsteverapp.newsaggregator.data.mapper

import com.myfirsteverapp.newsaggregator.data.remote.dto.ArticleDto
import com.myfirsteverapp.newsaggregator.domain.model.Article
import com.myfirsteverapp.newsaggregator.domain.model.Category
import com.myfirsteverapp.newsaggregator.domain.model.Source
import javax.inject.Inject

class ArticleMapper @Inject constructor() {

    fun mapDtoToDomain(dto: ArticleDto, category: Category): Article {
        return Article(
            id = dto.url?.hashCode()?.toString() ?: "",
            title = dto.title ?: "",
            description = dto.description,
            content = dto.content,
            url = dto.url ?: "",
            urlToImage = dto.urlToImage,
            author = dto.author,
            publishedAt = dto.publishedAt ?: "",
            source = Source(
                id = dto.source?.id,
                name = dto.source?.name ?: "Unknown"
            ),
            category = category,
            isBookmarked = false
        )
    }
}