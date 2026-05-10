package com.example.drivingapp.domain.usecase

import com.example.drivingapp.data.model.DrivingState
import com.example.drivingapp.data.repository.DrivingStateRepository

/**
 * Encapsulates the logic for manually toggling driving mode.
 *
 * Rules:
 *  - Toggle OFF → set manual override, suppress GPS detection
 *  - Toggle ON  → clear override, resume GPS detection
 *  - If GPS already shows Driving when override clears → restore Driving state
 */
class ToggleDrivingModeUseCase {

    operator fun invoke(enable: Boolean) {
        when {
            // User is turning driving mode OFF manually
            !enable -> {
                DrivingStateRepository.setManualOverrideOff(true)
                DrivingStateRepository.updateDrivingState(DrivingState.Stationary)
            }

            // User is turning driving mode ON manually
            enable -> {
                DrivingStateRepository.clearManualOverride()
                // Don't force Driving state — let GPS take over naturally
                // But if we have a recent speed reading, reflect it
                val currentSpeed = DrivingStateRepository.currentSpeedKmh.value
                if (currentSpeed >= 20f) {
                    DrivingStateRepository.updateDrivingState(
                        DrivingState.Driving(
                            speed = currentSpeed,
                            confidence = 0.5f,
                            durationSeconds = 0L
                        )
                    )
                } else {
                    DrivingStateRepository.updateDrivingState(DrivingState.Stationary)
                }
            }
        }
    }
}