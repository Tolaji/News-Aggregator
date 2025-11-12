package com.myfirsteverapp.newsaggregator.domain.model

enum class FreshnessFilter(val hours: Long?, val displayName: String) {
    LAST_6_HOURS(6, "6h"),
    LAST_24_HOURS(24, "24h"),
    LAST_3_DAYS(72, "3d"),
    ALL(null, "All")
}

