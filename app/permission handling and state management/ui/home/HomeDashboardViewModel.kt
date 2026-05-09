package com.example.drivingapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivingapp.data.model.DrivingState
import com.example.drivingapp.data.repository.StateChangeEvent
import com.example.drivingapp.domain.usecase.ObserveDrivingStateUseCase
import com.example.drivingapp.domain.usecase.ToggleDrivingModeUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeDashboardUiState(
    val drivingState: DrivingState       = DrivingState.Initializing,
    val currentSpeedDisplay: String      = "0 km/h",
    val isDrivingModeEnabled: Boolean    = false,
    val isManualOverrideOff: Boolean     = false,
    val confidencePercent: Int           = 0,
    val peakSpeedDisplay: String         = "0 km/h"
)

// One-shot UI effects — don't use StateFlow for these
sealed class UiEffect {
    object ShowOverrideActivatedSnackbar : UiEffect()
    object ShowOverrideClearedSnackbar : UiEffect()
    data class ShowPermissionDeniedBanner(val isPermanent: Boolean) : UiEffect()
}

class HomeDashboardViewModel(
    private val observeState: ObserveDrivingStateUseCase = ObserveDrivingStateUseCase(),
    private val toggleDrivingMode: ToggleDrivingModeUseCase = ToggleDrivingModeUseCase()
) : ViewModel() {

    // ── UI State ──────────────────────────────────────────────────────────────
    val uiState: StateFlow<HomeDashboardUiState> = combine(
        observeState.effectiveState(),
        observeState.currentSpeed(),
        observeState.isManualOverrideOff()
    ) { state, speedKmh, overrideOff ->
        HomeDashboardUiState(
            drivingState         = state,
            currentSpeedDisplay  = "${speedKmh.toInt()} km/h",
            isDrivingModeEnabled = state is DrivingState.Driving,
            isManualOverrideOff  = overrideOff,
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

    // ── One-shot effects ──────────────────────────────────────────────────────
    private val _uiEffects = MutableSharedFlow<UiEffect>()
    val uiEffects: SharedFlow<UiEffect> = _uiEffects.asSharedFlow()

    init {
        observeStateChangeEvents()
    }

    // ── Event observation — reacts to Repository events ───────────────────────
    private fun observeStateChangeEvents() {
        viewModelScope.launch {
            observeState.events().collect { event ->
                when (event) {
                    is StateChangeEvent.ManualOverrideActivated ->
                        _uiEffects.emit(UiEffect.ShowOverrideActivatedSnackbar)

                    is StateChangeEvent.ManualOverrideCleared ->
                        _uiEffects.emit(UiEffect.ShowOverrideClearedSnackbar)

                    is StateChangeEvent.PermissionChanged ->
                        if (!event.granted)
                            _uiEffects.emit(UiEffect.ShowPermissionDeniedBanner(false))

                    else -> Unit
                }
            }
        }
    }

    // ── User actions ──────────────────────────────────────────────────────────
    fun onDrivingModeToggled(enabled: Boolean) {
        toggleDrivingMode(enabled)
    }

    fun onPermissionPermanentlyDenied() {
        viewModelScope.launch {
            _uiEffects.emit(UiEffect.ShowPermissionDeniedBanner(isPermanent = true))
        }
    }
}