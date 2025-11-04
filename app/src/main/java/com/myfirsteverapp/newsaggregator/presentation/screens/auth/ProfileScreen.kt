// Kotlin
package com.myfirsteverapp.newsaggregator.presentation.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(
    uiState: AuthUiState,
    onSignOutClick: () -> Unit,
    onSignedOut: () -> Unit
) {
    LaunchedEffect(uiState.isAuthenticated) {
        if (!uiState.isAuthenticated) {
            onSignedOut()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val userName = uiState.currentUser?.displayName?.takeIf { it.isNotBlank() }
            ?: uiState.currentUser?.email
            ?: "Profile"
        Text(text = userName)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onSignOutClick) {
            Text("Sign out")
        }

        uiState.error?.let { err ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = err, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun ProfileRoute(
    onSignedOut: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    ProfileScreen(
        uiState = uiState,
        onSignOutClick = { viewModel.signOut() },
        onSignedOut = onSignedOut
    )
}
