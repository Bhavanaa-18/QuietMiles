package com.example.drivingapp.data.repository

import com.example.drivingapp.data.datastore.DrivingPreferences
import com.example.drivingapp.data.model.DrivingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * SINGLE SOURCE OF TRUTH for all driving-related state.
 *
 * Three layers:
 *  1. [drivingState]      — raw GPS/sensor-detected state
 *  2. [effectiveState]    — GPS state AFTER applying manual override
 *  3. [stateChangeEvents] — hot SharedFlow for one-shot callbacks
 *                           (Service and UI both observe this)
 */
object DrivingStateRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Injected after Application.onCreate()
    lateinit var preferences: DrivingPreferences

    // ── Raw GPS/sensor state ──────────────────────────────────────────────────
    private val _drivingState = MutableStateFlow<DrivingState>(DrivingState.Initializing)
    val drivingState: StateFlow<DrivingState> = _drivingState.asStateFlow()

    // ── Current speed ─────────────────────────────────────────────────────────
    private val _currentSpeedKmh = MutableStateFlow(0f)
    val currentSpeedKmh: StateFlow<Float> = _currentSpeedKmh.asStateFlow()

    // ── Manual override flag ──────────────────────────────────────────────────
    // true  = user manually turned OFF driving mode → GPS detection suppressed
    // false = normal GPS-driven detection
    private val _isManualOverrideOff = MutableStateFlow(false)
    val isManualOverrideOff: StateFlow<Boolean> = _isManualOverrideOff.asStateFlow()

    /**
     * Effective state = what the UI and Service actually act on.
     * If override is active, GPS-detected Driving state is suppressed.
     */
    val effectiveState: Flow<DrivingState> = combine(
        _drivingState,
        _isManualOverrideOff
    ) { detected, overrideOff ->
        when {
            overrideOff && detected is DrivingState.Driving -> DrivingState.Stationary
            else -> detected
        }
    }

    /**
     * Hot event bus — emits on EVERY state change.
     * Both UI (ViewModel) and Service observe this.
     * replay=1 ensures late subscribers get the last event.
     */
    private val _stateChangeEvents = MutableSharedFlow<StateChangeEvent>(replay = 1)
    val stateChangeEvents: SharedFlow<StateChangeEvent> = _stateChangeEvents.asSharedFlow()

    // ─────────────────────────────────────────────────────────────────────────
    // Write API — called by Service or Use Cases
    // ─────────────────────────────────────────────────────────────────────────

    fun updateDrivingState(state: DrivingState) {
        _drivingState.value = state
        emitEvent(StateChangeEvent.DetectedStateChanged(state))
    }

    fun updateSpeed(speedKmh: Float) {
        _currentSpeedKmh.value = speedKmh
    }

    /**
     * Manual override: user toggled driving mode OFF.
     * Suppresses GPS detection for the remainder of the trip.
     * Persisted to DataStore so it survives process death.
     */
    fun setManualOverrideOff(isOff: Boolean) {
        _isManualOverrideOff.value = isOff
        scope.launch {
            preferences.setManualOverrideOff(isOff)
        }
        emitEvent(
            if (isOff) StateChangeEvent.ManualOverrideActivated
            else StateChangeEvent.ManualOverrideCleared
        )
    }

    /**
     * Clear override — called when:
     * - User starts a new trip
     * - App detects device has been stationary for > 30 minutes
     */
    fun clearManualOverride() {
        _isManualOverrideOff.value = false
        scope.launch { preferences.clearManualOverride() }
        emitEvent(StateChangeEvent.ManualOverrideCleared)
    }

    fun reset() {
        _drivingState.value = DrivingState.Initializing
        _currentSpeedKmh.value = 0f
        // Do NOT reset override on service restart — it's intentional
    }

    // ── Restore persisted override on app launch ──────────────────────────────
    fun restoreFromPreferences() {
        scope.launch {
            preferences.isManualOverrideOff.collect { wasOverridden ->
                _isManualOverrideOff.value = wasOverridden
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Event emission
    // ─────────────────────────────────────────────────────────────────────────

    private fun emitEvent(event: StateChangeEvent) {
        scope.launch { _stateChangeEvents.emit(event) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Event types — exhaustive sealed class for type-safe callbacks
// ─────────────────────────────────────────────────────────────────────────────

sealed class StateChangeEvent {
    /** GPS/sensor detected a new state */
    data class DetectedStateChanged(val state: DrivingState) : StateChangeEvent()

    /** User manually switched driving mode OFF */
    object ManualOverrideActivated : StateChangeEvent()

    /** Override cleared — auto-detection resumed */
    object ManualOverrideCleared : StateChangeEvent()

    /** Permission status changed mid-session */
    data class PermissionChanged(val granted: Boolean) : StateChangeEvent()
}