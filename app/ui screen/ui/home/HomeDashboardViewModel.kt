package com.example.drivingapp.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeDashboardUiState(
    val isDrivingModeEnabled: Boolean = false,
    val currentSpeed: String = "0 km/h"
)

class HomeDashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeDashboardUiState())
    val uiState: StateFlow<HomeDashboardUiState> = _uiState.asStateFlow()

    fun toggleDrivingMode(enabled: Boolean) {
        _uiState.update { it.copy(isDrivingModeEnabled = enabled) }
    }

    fun updateSpeed(speed: String) {
        _uiState.update { it.copy(currentSpeed = speed) }
    }
}