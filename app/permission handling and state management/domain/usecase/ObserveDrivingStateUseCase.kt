package com.example.drivingapp.domain.usecase

import com.example.drivingapp.data.repository.DrivingStateRepository
import com.example.drivingapp.data.repository.StateChangeEvent
import kotlinx.coroutines.flow.Flow

/**
 * Clean abstraction for observing driving state.
 * ViewModels depend on this use case, not directly on the Repository.
 * Makes testing easier — just mock this class.
 */
class ObserveDrivingStateUseCase {

    /** Effective state — override already applied */
    fun effectiveState() = DrivingStateRepository.effectiveState

    /** Raw GPS state — before override */
    fun rawState() = DrivingStateRepository.drivingState

    /** Real-time speed */
    fun currentSpeed() = DrivingStateRepository.currentSpeedKmh

    /** Override flag */
    fun isManualOverrideOff() = DrivingStateRepository.isManualOverrideOff

    /**
     * One-shot event stream — for Service callbacks and one-time UI effects
     * (e.g. show a Snackbar when override activates)
     */
    fun events(): Flow<StateChangeEvent> = DrivingStateRepository.stateChangeEvents
}