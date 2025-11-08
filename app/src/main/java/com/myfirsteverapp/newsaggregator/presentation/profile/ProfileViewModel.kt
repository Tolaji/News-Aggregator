package com.myfirsteverapp.newsaggregator.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfirsteverapp.newsaggregator.domain.repository.NewsRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _bookmarkCount = MutableStateFlow(0)
    val bookmarkCount: StateFlow<Int> = _bookmarkCount.asStateFlow()

    init {
        loadUserProfile()
        loadBookmarkCount()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            _userEmail.value = currentUser?.email ?: "No email"
            _userName.value = currentUser?.displayName ?: extractNameFromEmail(currentUser?.email)
        }
    }

    private fun loadBookmarkCount() {
        viewModelScope.launch {
            newsRepository.getBookmarkedArticles().collect { articles ->
                _bookmarkCount.value = articles.size
            }
        }
    }

    private fun extractNameFromEmail(email: String?): String {
        return if (email != null) {
            val username = email.substringBefore("@")
            username.split(".", "_", "-")
                .joinToString(" ") { it.capitalize() }
        } else {
            "User"
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun refreshProfile() {
        loadUserProfile()
        loadBookmarkCount()
    }
}

// Extension function for capitalize (if not available)
private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}