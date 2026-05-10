package com.example.drivingapp.permission

/**
 * Represents the full permission status for all required permissions.
 * Each field is independent so the UI can show granular rationale.
 */
data class PermissionState(
    val fineLocation: Status = Status.NOT_REQUESTED,
    val backgroundLocation: Status = Status.NOT_REQUESTED,
    val activityRecognition: Status = Status.NOT_REQUESTED
) {
    enum class Status {
        NOT_REQUESTED,    // First launch — never asked
        GRANTED,          // User allowed
        DENIED,           // User denied once — can re-request
        PERMANENTLY_DENIED // User denied + "Don't ask again" — must go to Settings
    }

    /** True only when all critical permissions are granted */
    val canStartDrivingMode: Boolean
        get() = fineLocation == Status.GRANTED

    /** True when all optional + required permissions are granted */
    val isFullyGranted: Boolean
        get() = fineLocation == Status.GRANTED &&
                backgroundLocation == Status.GRANTED &&
                activityRecognition == Status.GRANTED

    /** Human-readable summary for debugging */
    override fun toString(): String =
        "PermissionState(" +
        "fineLocation=$fineLocation, " +
        "bg=$backgroundLocation, " +
        "activityRecognition=$activityRecognition)"
}