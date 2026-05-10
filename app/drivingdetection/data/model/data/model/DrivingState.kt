package com.example.drivingapp.data.model

import android.location.Location

sealed class DrivingState {

    object Unknown : DrivingState()

    object Stationary : DrivingState()

    /**
     * Speed 5–20 km/h — moving but below driving threshold.
     * Prevents false resets when slowing at traffic lights.
     */
    object SlowMoving : DrivingState()                        // ← NEW

    data class Driving(
        val speed: Float,
        val confidence: Float,
        val durationSeconds: Long,
        val location: Location? = null,                       // ← NEW (optional, for map use)
        val peakSpeedKmh: Float = speed                       // ← NEW (track session max)
    ) : DrivingState()

    data class Error(
        val reason: String,
        val cause: ErrorCause = ErrorCause.UNKNOWN            // ← NEW (typed errors)
    ) : DrivingState()

    // ── Typed error causes — avoids stringly-typed error handling ────────────
    enum class ErrorCause {                                   // ← NEW
        PERMISSION_REVOKED,
        GPS_UNAVAILABLE,
        SENSOR_UNAVAILABLE,
        UNKNOWN
    }
}