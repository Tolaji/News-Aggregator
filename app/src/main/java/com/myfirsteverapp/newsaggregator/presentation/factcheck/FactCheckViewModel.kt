package com.myfirsteverapp.newsaggregator.presentation.factcheck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfirsteverapp.newsaggregator.domain.model.FactCheck
import com.myfirsteverapp.newsaggregator.domain.model.FactCheckSamples
import com.myfirsteverapp.newsaggregator.domain.model.VerificationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FactCheckViewModel @Inject constructor() : ViewModel() {

    private val allFactChecks = FactCheckSamples.getSampleFactChecks()

    private val _factChecks = MutableStateFlow<List<FactCheck>>(allFactChecks)
    val factChecks: StateFlow<List<FactCheck>> = _factChecks.asStateFlow()

    private val _selectedFilter = MutableStateFlow<VerificationStatus?>(null)
    val selectedFilter: StateFlow<VerificationStatus?> = _selectedFilter.asStateFlow()

    fun filterByStatus(status: VerificationStatus?) {
        viewModelScope.launch {
            _selectedFilter.value = status
            _factChecks.value = if (status == null) {
                allFactChecks
            } else {
                allFactChecks.filter { it.status == status }
            }
        }
    }

    // TODO: Implement actual fact check fetching from API/Firestore
    // For now using sample data
}