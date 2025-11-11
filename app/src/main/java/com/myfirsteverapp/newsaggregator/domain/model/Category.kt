package com.myfirsteverapp.newsaggregator.domain.model

enum class Category(val displayName: String) {
    ALL("All"),
    BUSINESS("Business"),
    TECH("Technology"),
    SPORTS("Sports"),
    ENTERTAINMENT("Entertainment"),
    HEALTH("Health"),
    SCIENCE("Science"),
    POLITICS("Politics");

    companion object {
        fun getApiCategory(category: Category): String? {
            return when (category) {
                ALL -> null
                BUSINESS -> "business"
                TECH -> "technology"
                SPORTS -> "sports"
                ENTERTAINMENT -> "entertainment"
                HEALTH -> "health"
                SCIENCE -> "science"
                POLITICS -> "politics"
            }
        }
    }
}