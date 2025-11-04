package com.myfirsteverapp.newsaggregator.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfirsteverapp.newsaggregator.data.remote.firebase.FirebaseAuthManager
import com.myfirsteverapp.newsaggregator.data.remote.firebase.FirestoreManager
import com.myfirsteverapp.newsaggregator.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null,
    val currentUser: User? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authManager: FirebaseAuthManager,
    private val firestoreManager: FirestoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authManager.currentUser.collect { firebaseUser ->
                _uiState.update {
                    it.copy(
                        isAuthenticated = firebaseUser != null,
                        currentUser = firebaseUser?.let { user ->
                            User(
                                uid = user.uid,
                                email = user.email ?: "",
                                displayName = user.displayName,
                                photoUrl = user.photoUrl?.toString(),
                                createdAt = user.metadata?.creationTimestamp ?: 0L
                            )
                        }
                    )
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        // Demonstrates conditionals
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Email and password are required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            authManager.signIn(email, password)
                .onSuccess { firebaseUser ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            currentUser = User(
                                uid = firebaseUser.uid,
                                email = firebaseUser.email ?: "",
                                displayName = firebaseUser.displayName,
                                photoUrl = firebaseUser.photoUrl?.toString(),
                                createdAt = firebaseUser.metadata?.creationTimestamp ?: 0L
                            )
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.localizedMessage ?: "Sign in failed"
                        )
                    }
                }
        }
    }

    fun signUp(email: String, password: String, displayName: String) {
        // Input validation using conditionals
        if (email.isBlank() || password.isBlank() || displayName.isBlank()) {
            _uiState.update { it.copy(error = "All fields are required") }
            return
        }

        if (password.length < 6) {
            _uiState.update { it.copy(error = "Password must be at least 6 characters") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            authManager.signUp(email, password, displayName)
                .onSuccess { firebaseUser ->
                    // Create user profile in Firestore
                    val user = User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        displayName = firebaseUser.displayName,
                        photoUrl = null,
                        createdAt = System.currentTimeMillis()
                    )

                    firestoreManager.createUserProfile(user)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            currentUser = user
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.localizedMessage ?: "Sign up failed"
                        )
                    }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authManager.signOut()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}