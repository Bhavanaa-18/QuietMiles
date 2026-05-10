package com.example.drivingapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivingapp.data.model.DrivingState
import com.example.drivingapp.data.repository.DrivingStateRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeDashboardUiState(
    val drivingState: DrivingState = DrivingState.Initializing,
    val currentSpeedDisplay: String = "0 km/h",
    val isDrivingModeEnabled: Boolean = false,
    val confidencePercent: Int = 0,
    val peakSpeedDisplay: String = "0 km/h"
)

class HomeDashboardViewModel : ViewModel() {

    val uiState: StateFlow<HomeDashboardUiState> = combine(
        DrivingStateRepository.drivingState,
        DrivingStateRepository.currentSpeedKmh
    ) { state, speedKmh ->
        HomeDashboardUiState(
            drivingState         = state,
            currentSpeedDisplay  = "${speedKmh.toInt()} km/h",
            isDrivingModeEnabled = state is DrivingState.Driving,
            confidencePercent    = if (state is DrivingState.Driving)
                                       (state.confidence * 100).toInt() else 0,
            peakSpeedDisplay     = if (state is DrivingState.Driving)
                                       "${state.peakSpeedKmh.toInt()} km/h" else "0 km/h"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeDashboardUiState()
    )

    fun toggleDrivingMode(enabled: Boolean) {
        DrivingStateRepository.updateDrivingState(
            if (enabled) DrivingState.Driving(0f, 1f, 0L)
            else DrivingState.Stationary
        )
    }
}