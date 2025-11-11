package com.myfirsteverapp.newsaggregator.domain.model

import com.myfirsteverapp.newsaggregator.domain.model.Category

data class User(
    val uid: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val createdAt: Long,
    val preferences: UserPreferences = UserPreferences()
)

data class UserPreferences(
    val favoriteCategories: List<Category> = emptyList(),
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val autoRefresh: Boolean = true
)